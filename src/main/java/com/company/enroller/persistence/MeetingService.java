package com.company.enroller.persistence;

import java.util.Collection;

import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.springframework.stereotype.Component;

import com.company.enroller.model.Meeting;

@Component("meetingService")
public class MeetingService {

	DatabaseConnector connector;

	public MeetingService() {
		connector = DatabaseConnector.getInstance();
	}

	public Collection<Meeting> getAll() {
		String hql = "FROM Meeting";
		Query query = connector.getSession().createQuery(hql);
		return query.list();
	}

	public Collection<Meeting> getAllSortByTitle() {
		String hql = "FROM Meeting ORDER BY title";
		Query query = connector.getSession().createQuery(hql);
		return query.list();
	}

	public Meeting findById (long id) {
		return (Meeting) connector.getSession().get(Meeting.class, id);
	}

	public Meeting findByTitle (String title) {
		return (Meeting) connector.getSession().get(Meeting.class, title);
	}

	public Meeting findByDescription (String description) {
		return (Meeting) connector.getSession().get(Meeting.class, description);
	}

	public Meeting findByParticipant (String participantLogin) {
		return (Meeting) connector.getSession().get(Meeting.class, participantLogin);
	}

	public Meeting addNewMeeting(Meeting meeting) {
		Transaction transaction = connector.getSession().beginTransaction();
		connector.getSession().save(meeting);
		transaction.commit();
		return meeting;
	}

	public void deleteMeeting(Meeting meeting) {
		Transaction transaction = connector.getSession().beginTransaction();
		connector.getSession().delete(meeting);
		transaction.commit();
	}

	public void updateMeeting(Meeting meeting) {
		Transaction transaction = connector.getSession().beginTransaction();
		connector.getSession().update(meeting);
		transaction.commit();
	}

}
