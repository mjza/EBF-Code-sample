package com.mjzsoft.odata.services;

import java.util.Optional;

public interface BaseService<T> {
	public Class<T> getType();
    public Optional<T> findById(String id);
}