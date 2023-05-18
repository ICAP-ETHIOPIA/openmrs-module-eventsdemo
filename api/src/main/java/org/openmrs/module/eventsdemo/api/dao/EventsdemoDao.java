/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.eventsdemo.api.dao;

import java.util.List;

import org.hibernate.criterion.Restrictions;
import org.openmrs.api.db.hibernate.DbSession;
import org.openmrs.api.db.hibernate.DbSessionFactory;
import org.openmrs.module.eventsdemo.Item;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository("eventsdemo.EventsdemoDao")
public class EventsdemoDao {
	
	@Autowired
	DbSessionFactory sessionFactory;
	
	private DbSession getSession() {
		return sessionFactory.getCurrentSession();
	}
	
	public Item getItemByUuid(String uuid) {
		return (Item) getSession().createCriteria(Item.class).add(Restrictions.eq("uuid", uuid)).uniqueResult();
	}
	
	public Item saveItem(Item item) {
		getSession().saveOrUpdate(item);
		return item;
	}
	
	public void deleteItem(Item item) {
		getSession().delete(item);
	}
	
	public List<Item> getAllItems() {
		return getSession().createCriteria(Item.class).list();
	}
}
