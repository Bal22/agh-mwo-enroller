package com.company.enroller.controllers;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.company.enroller.model.Participant;
import com.company.enroller.persistence.ParticipantService;

@RestController
@RequestMapping("/participants")
public class ParticipantRestController {

	@Autowired
	ParticipantService participantService;

	@RequestMapping(value = "", method = RequestMethod.GET)
	public ResponseEntity<?> getParticipants() {
		Collection<Participant> participants = participantService.getAll();
		return new ResponseEntity<Collection<Participant>>(participants, HttpStatus.OK);
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public ResponseEntity<?> getParticipant(@PathVariable("id") String login) {
		Participant participant = participantService.findByLogin(login);
		if (participant == null) {
			return new ResponseEntity(HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<Participant>(participant, HttpStatus.OK);
	}

	@RequestMapping(value = "", method = RequestMethod.POST)
	public ResponseEntity<?> registerParticipant(@RequestBody Participant participant) {
		Participant foundParticipant = participantService.findByLogin(participant.getLogin());
		if  (foundParticipant != null) {
			return new ResponseEntity<String>("Unable to create. Already exists", HttpStatus.CONFLICT);
		}

		participantService.add(participant);
		return new ResponseEntity<Participant>(participant, HttpStatus.OK);
	}

	@RequestMapping(value = "{id}", method = RequestMethod.DELETE)
	public ResponseEntity<?> removeParticipant(@PathVariable("id") String login) {

		Participant participant = participantService.findByLogin(login);
		if (participant == null) {
			return new ResponseEntity<>("Not found", HttpStatus.NOT_FOUND);
		}
		participantService.remove(participant);
		return new ResponseEntity<Participant> (participant,HttpStatus.OK);
	}

	@RequestMapping(value = "{id}", method = RequestMethod.PUT)
	public ResponseEntity<?> updateParticipant(@PathVariable("id") String login, @RequestBody Participant participant) {

		Participant foundParticipant = participantService.findByLogin(login);
		if (foundParticipant == null) {
			return new ResponseEntity<>("Not found", HttpStatus.NOT_FOUND);
		}
		foundParticipant.setPassword(participant.getPassword());
		participantService.update(foundParticipant);
		return new ResponseEntity<Participant> (participant,HttpStatus.OK);
	}

	

}
