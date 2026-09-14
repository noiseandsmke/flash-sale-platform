---@diagnostic disable: undefined-global
-- luacheck: globals KEYS ARGV redis

local lockKey = KEYS[1]
local userId = ARGV[1]
local ttlSeconds = tonumber(ARGV[2])

if redis.call('EXISTS', lockKey) == 1 then
    return 0
end

redis.call('SET', lockKey, userId, 'EX', ttlSeconds)
return 1
