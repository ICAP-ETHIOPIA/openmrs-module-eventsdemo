/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.eventsdemo.api.impl;

import java.util.List;

import org.openmrs.api.APIException;
import org.openmrs.api.UserService;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.eventsdemo.Item;
import org.openmrs.module.eventsdemo.api.EventsdemoService;
import org.openmrs.module.eventsdemo.api.dao.EventsdemoDao;
import org.springframework.beans.factory.annotation.Autowired;

public class EventsdemoServiceImpl extends BaseOpenmrsService implements EventsdemoService {
	
	@Autowired
	EventsdemoDao dao;
	
	UserService userService;
	
	/**
	 * Injected in moduleApplicationContext.xml
	 */
	public void setDao(EventsdemoDao dao) {
		this.dao = dao;
	}
	
	/**
	 * Injected in moduleApplicationContext.xml
	 */
	public void setUserService(UserService userService) {
		this.userService = userService;
	}
	
	@Override
	public Item getItemByUuid(String uuid) throws APIException {
		return dao.getItemByUuid(uuid);
	}
	
	@Override
	public Item saveItem(Item item) throws APIException {
		if (item.getOwner() == null) {
			item.setOwner(userService.getUser(1));
		}
		
		return dao.saveItem(item);
	}
	
	public void setFlag(boolean isSent, String uuid) throws APIException {
		dao.setFlag(isSent, uuid);
	}
	
	@Override
	public void deleteItem(Item item) throws APIException {
		dao.deleteItem(item);
	}
	
	@Override
	public List<Item> getAllItems() throws APIException {
		return dao.getAllItems();
	}
	
	public List<Item> getAllUnsentItems() throws APIException {
		return dao.getAllUnsentItems();
	}
}
