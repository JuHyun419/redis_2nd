local rateLimitKey = KEYS[1]
local blockedKey = KEYS[2]

local requestLimitCount = tonumber(ARGV[1])
local blockTime = tonumber(ARGV[2])
local rateLimitTTL = tonumber(ARGV[3])

if redis.call("EXISTS", blockedKey) == 1 then
    return -1
end

local current = redis.call("INCR", rateLimitKey)
if current == 1 then
    redis.call("EXPIRE", rateLimitKey, rateLimitTTL)
end

if current > requestLimitCount then
    redis.call("SET", blockedKey, "BLOCKED", "EX", blockTime)
    return -2
end

return current
