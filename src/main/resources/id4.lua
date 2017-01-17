local lock_key = 'id-generator-lock'
local sequence_key = 'id-generator-sequence'
local logical_shard_id_key = 'id-generator-logical-shard-id'

local max_sequence = 4095 --自增序列的最大值
local min_logical_shard_id = 0 --最小的分片ID
local max_logical_shard_id = 1023 --最大的分片ID

local num_ids = tonumber(ARGV[1]) --需要获取的ID数量

--如果存在锁标识说明当前毫秒下的自增序列已经分配完毕，必须等到下一个毫秒才能分配新的序列
if redis.call('EXISTS', lock_key) == 1 then
  redis.log(redis.LOG_NOTICE, 'Cannot generate ID, waiting for lock to expire.')
  return redis.error_reply('Cannot generate ID, waiting for lock to expire.')
end

local logical_shard_id = tonumber(redis.call('GET', logical_shard_id_key)) or 1 --分片ID
--检查分片ID
if logical_shard_id < min_logical_shard_id or logical_shard_id > max_logical_shard_id then
  redis.log(redis.LOG_NOTICE, 'Cannot generate ID, logical_shard_id invalid.')
  return redis.error_reply('Cannot generate ID, logical_shard_id invalid.')
end

if num_ids > max_sequence then
  redis.log(redis.LOG_NOTICE, 'Cannot generate ID, num_ids too large.')
  return redis.error_reply('Cannot generate ID, num_ids too large.')
end

local num_ids = tonumber(ARGV[1]) --需要获取的ID数量
local end_sequence = redis.call('INCRBY', sequence_key, num_ids) --最大的序列
local start_sequence = end_sequence - num_ids + 1 --最小的序列

if end_sequence >= max_sequence then
  --[[
  如果生成的序列大于最大的序列值，设置锁标识并设置过期时间为1毫秒
  --]]
  redis.log(redis.LOG_NOTICE, 'Rolling sequence back to the start, locking for 1ms.')
  redis.call('SET', sequence_key, '-1')
  redis.call('PSETEX', lock_key, 1, 'lock')
  end_sequence = max_sequence
end

--将移位计算交给客户端实现
local current_time = redis.call('TIME')
return {
  start_sequence,
  end_sequence,
  logical_shard_id,
  tonumber(current_time[1]) * 1000 + math.floor(tonumber(current_time[2]) / 1000)
}