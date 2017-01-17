local lock_key = 'id-generator-lock'
local logical_shard_id_key = 'id-generator-logical-shard-id'

local max_sequence = 4095 --自增序列的最大值
local min_logical_shard_id = 0 --最小的分片ID
local max_logical_shard_id = 1023 --最大的分片ID

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

--根据当前时间生成主键key
local now = redis.call('TIME')
local sequence_key = 'id-generator-sequence' .. '-' .. now[1] .. '-' .. math.floor(now[2]/1000);

local sequence
--如果自增序列大于最大的序列，说明当前毫秒的序列已经分配完毕，使用循环重试的方法直到时间跳到下一毫秒
repeat
      sequence = tonumber(redis.call('INCRBY', sequence_key, 1))
      if sequence > max_sequence then
              now = redis.call('TIME')
              sequence_key = 'id-generator-sequence' .. '-' .. now[1] .. '-' .. math.floor(now[2]/1000);
      end
until sequence <= max_sequence

--将移位计算交给客户端实现
return {
  sequence,
  logical_shard_id,
  last_time
}