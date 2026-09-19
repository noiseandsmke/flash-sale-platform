---@diagnostic disable: undefined-global
-- luacheck: globals KEYS ARGV redis

local lockKey = KEYS[1]
local expectedUserId = ARGV[1]

if redis.call('GET', lockKey) == expectedUserId then
    return redis.call('DEL', lockKey)
else
    return 0
end
