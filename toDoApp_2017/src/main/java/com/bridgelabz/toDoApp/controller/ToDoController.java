package com.bridgelabz.toDoApp.controller;

import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.bridgelabz.toDoApp.model.ToDo;
import com.bridgelabz.toDoApp.model.User;
import com.bridgelabz.toDoApp.service.serviceInterface.ToDoService;

/**
 * this is the Controller class,it handles some the requests which are used to
 * managing the ToDo
 * 
 * @author bridgeit vinay
 */
@RestController
public class ToDoController {

	@Autowired
	private ToDoService toDoService;

	/**
	 * to add the new note
	 * 
	 * @param toDo
	 * @param request
	 * @param response
	 * @return String message and status
	 */
	@RequestMapping(value = "addNote", method = RequestMethod.POST)
	public ResponseEntity<String> addNote(@RequestBody ToDo toDo, HttpServletRequest request,
			HttpServletResponse response) {

		HttpSession session = request.getSession(false);

		if (session != null) {

			User user = (User) session.getAttribute("user");
			toDo.setUser(user);

			boolean result = toDoService.addNote(toDo);

			if (result) {
				return new ResponseEntity<String>("{status:'success', todo:{'id'"+toDo.getId()+"}}", HttpStatus.OK);
			} else {
				return new ResponseEntity<String>("{status:'failure', message:'ToDo Data not saved'}",
						HttpStatus.NOT_ACCEPTABLE);
			}
		}

		else {
			return new ResponseEntity<String>("{status:'failure', message:'signIn first'}", HttpStatus.NOT_ACCEPTABLE);
		}
	}

	/**
	 * to get all the notes by using the usedId
	 * 
	 * @param UserId
	 * @param request
	 * @param response
	 * @return String message and status
	 */
	@RequestMapping(value = "getNotes/{id}", method = RequestMethod.GET)
	public ResponseEntity<String> getNotes(@PathVariable("id") int userId, HttpServletRequest request,
			HttpServletResponse response) {

		HttpSession session = request.getSession(false);

		if (session != null) {
			List<ToDo> todoList = toDoService.getNotes(userId);
			if (!todoList.isEmpty()) {

				Iterator<ToDo> i = todoList.iterator();
				while (i.hasNext()) {
					ToDo do1 = i.next();
					System.out.println("Note id: " + do1.getId());
					System.out.println("Title: " + do1.getTitle());
					System.out.println("Note : " + do1.getNote());
					System.out.println("remainder:" + do1.getRemainder());
					System.out.println("update in " + do1.getUpDated() + "\n");
				}

				return new ResponseEntity<String>("data are fetched", HttpStatus.OK);
			} else {
				return new ResponseEntity<String>("notes are not added", HttpStatus.NOT_FOUND);
			}
		} else {
			return new ResponseEntity<String>("SignIn first", HttpStatus.NOT_FOUND);
		}
	}

	/**
	 * to delete the particular note
	 * 
	 * @param id
	 * @param request
	 * @param response
	 * @return String message and status
	 */
	@RequestMapping(value = "deleteNote/{id}", method = RequestMethod.DELETE)
	public ResponseEntity<String> deleteNote(@PathVariable("id") int id, HttpServletRequest request,
			HttpServletResponse response) {

		HttpSession session = request.getSession(false);
		if (session != null) {

			int result = toDoService.deleteNote(id);

			if (result != 0) {
				return new ResponseEntity<String>("note Deleted", HttpStatus.OK);
			} else {
				return new ResponseEntity<String>("note not present", HttpStatus.NOT_FOUND);
			}
		}
		return new ResponseEntity<String>("signIn first", HttpStatus.BAD_REQUEST);
	}

	/**
	 * to update the note
	 * 
	 * @param toDo
	 * @param toDoid
	 * @param request
	 * @param response
	 * @return String message and status
	 */
	@RequestMapping(value = "editNote/{id}", method = RequestMethod.PUT)
	public ResponseEntity<String> updateNote(@RequestBody ToDo toDo, @PathVariable("id") int toDoid,
			HttpServletRequest request, HttpServletResponse response) {

		HttpSession session = request.getSession(false);

		if (session != null) {

			User user = (User) session.getAttribute("user");
			toDo.setUser(user);
			toDo.setId(toDoid);

			List<ToDo> toDoList = toDoService.getNotes(user.getId());

			Iterator<ToDo> i = toDoList.iterator();
			boolean flag = false;

			while (i.hasNext()) {
				ToDo do1 = i.next();
				if (do1.getId() == toDo.getId()) {
					flag = true;
				}
			}

			if (!toDoList.isEmpty() && flag) {
				boolean result = toDoService.updateNote(toDo);
				if (result) {
					return new ResponseEntity<String>("updated", HttpStatus.OK);
				} else {
					return new ResponseEntity<String>("not updated", HttpStatus.NOT_ACCEPTABLE);
				}
			} else {
				return new ResponseEntity<String>("you dont have any notes", HttpStatus.NOT_ACCEPTABLE);
			}
		} else {
			return new ResponseEntity<String>("Login first", HttpStatus.NOT_ACCEPTABLE);
		}
	}

}
