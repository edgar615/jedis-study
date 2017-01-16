--两种规则，1秒钟1次请求，每分钟5次请求，为简单起见，规则未使用参数传入
local current_timestamp = tonumber(ARGV[1]) -- 当前请求的时间
local key = "rate.limit2:" .. KEYS[1] --限流KEY

--删除列表中超过1分钟的请求
redis.pcall('ZREMRANGEBYSCORE', key, 0, current_timestamp - 60)

local passed1 = true
local passed2 = true
--获取第一个请求
local last_drip = redis.pcall('ZREVRANGEBYSCORE', key, '+inf', '-inf', 'LIMIT', 0, 1)

--比较上次的时间戳和当前请求的时间戳
if last_drip[1] then
    --上次请求的时间
    local last_time = tonumber(last_drip[1])
    passed1 = current_timestamp >=  (last_time + 1)
end

--获取前5个请求
local last_drip = redis.pcall('ZREVRANGEBYSCORE', key, '+inf', '-inf', 'LIMIT', 0, 5)

--比较上次的时间戳和当前请求的时间戳
if last_drip[5] then
    --第5次请求的时间
    local last_time = tonumber(last_drip[5])
    passed2  = current_timestamp >=  (last_time + 60)
end

if passed1 and passed2 then
  redis.pcall('ZADD', key, current_timestamp, current_timestamp)
end
--设置1分钟过期
redis.call('EXPIRE', key, 60)
return {current_timestamp,last_drip, passed1, passed2}