package com.company.enroller.controllers;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import com.company.enroller.model.Participant;
import com.company.enroller.persistence.ParticipantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.company.enroller.model.Meeting;
import com.company.enroller.persistence.MeetingService;


@RestController
@RequestMapping("/meetings")
public class MeetingRestController {

    private MeetingService meetingService;
    private ParticipantService participantService;

    @Autowired
    public MeetingRestController(MeetingService meetingService, ParticipantService participantService) {
        this.meetingService = meetingService;
        this.participantService = participantService;
    }

    @RequestMapping(value = "", method = RequestMethod.GET)
    public ResponseEntity<?> getMeetings() {
        Collection<Meeting> meetings = meetingService.getAll();
        return new ResponseEntity<Collection<Meeting>>(meetings, HttpStatus.OK);
    }

    @RequestMapping(value = "/sort", method = RequestMethod.GET)
    public ResponseEntity<?> getSortedMeetings() {
        Collection<Meeting> meetings = meetingService.getAllSortByTitle();
        return new ResponseEntity<Collection<Meeting>>(meetings, HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<?> getMeetingById(@PathVariable("id") long id) {
        Meeting meeting = meetingService.findById(id);
        if (meeting == null) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<Meeting>(meeting, HttpStatus.OK);
    }

    @RequestMapping(value = "title/{title}", method = RequestMethod.GET)
    public ResponseEntity<?> searchByTitle(@PathVariable("title") String title) {
        Collection<Meeting> meetings = meetingService.getAll();
        List<Meeting> found = meetings.stream().filter(meeting -> meeting.getTitle().contains(title)).collect(Collectors.toList());

        if (found.isEmpty()) {
            return new ResponseEntity<>("Meeting title not found", HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<Collection<Meeting>>(found, HttpStatus.OK);
    }

    @RequestMapping(value = "desc/{description}", method = RequestMethod.GET)
    public ResponseEntity<?> searchByDescription(@PathVariable("description") String description) {
        Collection<Meeting> meetings = meetingService.getAll();
        List<Meeting> found = meetings.stream().filter(meeting -> meeting.getDescription().contains(description)).collect(Collectors.toList());

        if (found.isEmpty()) {
            return new ResponseEntity<>("Meeting description not found", HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<Collection<Meeting>>(found, HttpStatus.OK);
    }

    @RequestMapping(value = "user/{userLogin}", method = RequestMethod.GET)
    public ResponseEntity<?> searchByParticipantLogin(@PathVariable("userLogin") String login) {
        Collection<Meeting> meetings = meetingService.getAll();
        Participant participant = participantService.findByLogin(login);
        if (participant == null) {
            return new ResponseEntity<>("Participant not found", HttpStatus.NOT_FOUND);
        }
        List<Meeting> found = meetings.stream().filter(meeting -> meeting.getParticipants().contains(participant)).collect(Collectors.toList());
        return new ResponseEntity<Collection<Meeting>>(found, HttpStatus.OK);
    }

    @RequestMapping(value = "", method = RequestMethod.POST)
    public ResponseEntity<?> registerNewMeeting(@RequestBody Meeting meeting) {

        Meeting newMeeting = meetingService.addNewMeeting(meeting);
        return new ResponseEntity<Meeting>(newMeeting, HttpStatus.OK);

    }

    @RequestMapping(value = "{id}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteMeetingById(@PathVariable("id") long id) {
        Meeting meeting = meetingService.findById(id);
        if (meeting == null) {
            return new ResponseEntity<>("Meeting id " + id + " doesn't exist", HttpStatus.NOT_FOUND);
        }
        meetingService.deleteMeeting(meeting);
        return new ResponseEntity<>("Meeting deleted", HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}/{login}", method = RequestMethod.POST)
    public ResponseEntity<?> addParticipantToMeeting(@PathVariable("id") long id, @PathVariable("login") String login) {
        Meeting meeting = meetingService.findById(id);
        Participant participant = participantService.findByLogin(login);

        if(meeting.getParticipants().contains(participant)) {
            return new ResponseEntity<>("Participant already added to meeting", HttpStatus.CONFLICT);
        } else if (participant == null) {
            return new ResponseEntity<>("Participant does not exist", HttpStatus.CONFLICT);
        }
        meeting.addParticipant(participant);
        meetingService.updateMeeting(meeting);
        return new ResponseEntity<Collection<Participant>>(meeting.getParticipants(), HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}/participants", method = RequestMethod.GET)
    public ResponseEntity<?> getParticipantsFromMeeting(@PathVariable("id") long id) {
        Meeting meeting = meetingService.findById(id);
        if(meeting == null) {
            return new ResponseEntity<>("Meeting does not exist", HttpStatus.CONFLICT);
        }
        return new ResponseEntity<Collection<Participant>>(meeting.getParticipants(), HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public ResponseEntity<?> updateMeetingData(@PathVariable("id") long id, @RequestBody Meeting meeting) {
        Meeting meetingToUpdate = meetingService.findById(id);
        if(meetingToUpdate == null) {
            return new ResponseEntity<>("Meeting does not exist", HttpStatus.CONFLICT);
        }
        meetingToUpdate.setTitle(meeting.getTitle());
        meetingToUpdate.setDescription(meeting.getDescription());
        meetingToUpdate.setDate(meeting.getDate());
        meetingService.updateMeeting(meetingToUpdate);

        return new ResponseEntity<Meeting>(meeting, HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}/{login}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteParticipant(@PathVariable("id") long id, @PathVariable("login") String login) {
        Meeting meeting = meetingService.findById(id);
        Participant participant = participantService.findByLogin(login);

        if (participant == null) {
            return new ResponseEntity<>("Specified user deosn't participate in this meeting", HttpStatus.CONFLICT);
        }
        meeting.removeParticipant(participant);
        meetingService.updateMeeting(meeting);

        return new ResponseEntity<>("Participant " + login + " removed from meeting nr " + id, HttpStatus.OK);
    }

}
