/*
 * Copyright 2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.antennae.server.notifier.repository;

import java.util.List;

import org.antennae.server.notifier.entities.User;

public interface IUserDao {
	
	public void addUser( User user);
	public void updateUser( User user );
	public User getUser( int id);
	public void deleteUser( int id);

	public boolean validateUserPassword(String userId, String password);
	public List<User> getUsersByLoginId( String userId );
	
	public List<User> getAllUsers();
	public List<User> getUsers(List<Integer> userIds);
	public List<User> getUsersForLoginIds(List<String> loginIds);
}