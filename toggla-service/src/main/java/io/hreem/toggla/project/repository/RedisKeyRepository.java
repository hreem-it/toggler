package io.hreem.toggla.project.repository;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import io.hreem.toggla.common.Util;
import io.hreem.toggla.common.repository.ObjectNotFoundException;
import io.hreem.toggla.common.repository.Repository;
import io.quarkus.redis.client.RedisClient;
import io.hreem.toggla.common.repository.DBTypeQualifiers;
import io.hreem.toggla.common.repository.DataTypeQualifiers;

@ApplicationScoped
@DBTypeQualifiers.Redis
@DataTypeQualifiers.APIKey
public class RedisKeyRepository implements Repository<String, String> {

    @Inject
    Util util;

    @Inject
    RedisClient redis;

    @Override
    public List<String> getAllKeysMatching(String pattern) {
        final var response = redis.keys(pattern);
        return response.stream()
                .map(toggleKeyResponse -> toggleKeyResponse.toString())
                .toList();
    }

    @Override
    public void create(String id, String data) {
        redis.set(List.of(id, data));
    }

    @Override
    public void update(String id, String data) {
        redis.set(List.of(id, data));
    }

    @Override
    public void delete(String id) throws ObjectNotFoundException {
        if (this.exists(id)) {
            redis.del(List.of(id));
        } else {
            throw new ObjectNotFoundException("Object with id " + id + " does not exist");
        }
    }

    @Override
    public String get(String id) throws ObjectNotFoundException {
        final var result = redis.get(id);
        if (result == null) {
            throw new ObjectNotFoundException("Object with id " + id + " does not exist");
        }
        return result.toString();
    }

    @Override
    public boolean exists(String id) {
        return redis.exists(List.of(id)).toBoolean();
    }

}