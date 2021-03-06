package com.example;

import java.io.IOException;
import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@WebServlet("/FilmServlet")
public class FilmServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
  @EJB
  FilmInterface hw;  
	
  public FilmServlet() {
    super();
  }

	
  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    String result = "Servlet asks EJB and gets response: ";
    
    result = result + "\n " +  hw.updateNoteWithDelay2(147, "xyz");

    int newNoteId = hw.newNote("nowa notatka z serwletu", 1);
    result = result + "\n " + newNoteId;
    
    for (Object[] row : hw.getNotes()) {
      result = result + "\n " + (Integer) row[0] + " " + (String) row[1] + " " + (Integer) row[2] + " " + (String) row[3];
    }
    
    hw.updateNote(newNoteId, "update from the bean");
    
    for (Object[] row : hw.getNotes()) {
      result = result + "\n " + (Integer) row[0] + " " + (String) row[1] + " " + (Integer) row[2] + " " + (String) row[3];
    }
    
    for (Object[] row : hw.getNoteToCategory()) {
      result = result + "\n " + (Integer) row[0] + " " + (String) row[1] + " " + (Integer) row[2] + " " + (String) row[3];
    }
    
    result = result + "\n ";
    
    hw.addNoteToCategory (60, 1);
    
    for (Object[] row : hw.getNoteToCategory()) {
      result = result + "\n " + (Integer) row[0] + " " + (String) row[1] + " " + (Integer) row[2] + " " + (String) row[3];
    }
    
    result = result + "\n ";
    
    hw.removeNoteToCategory (60, 1);
    
    for (Object[] row : hw.getNoteToCategory()) {
      result = result + "\n " + (Integer) row[0] + " " + (String) row[1] + " " + (Integer) row[2] + " " + (String) row[3];
    }
    
    result = result + "\n ";
    
    for (Note n : hw.getNoteToCategory2()) {
      for (Category c: n.categories)	{
      	result = result + "\n " + n.noteId + " " + n.note + " " + c.id + " " + c.name;	
      }      
    }
 
    result = result + "\n ";
    
    for (Note n : hw.getNotes2()) {
      result = result + "\n " + n.noteId + " " + n.note + " " + n.project.id  + " " + n.project.name;
    }
    
    result = result + "\n ";
    
    // int newUserId = hw.addAdminUser("Zenek", 4);
    // result = result + "\n " + newUserId;
    
    result = result + "\n ";
    
    for (AdminUser u : hw.getAdminUsers()) {
      result = result + "\n " + u.id + " " + u.login + " " + u.permissionLevel;
    }
    
    result = result + "\n ";
    
    for (Object[] row : hw.getAdminUsersWithGrouping()) {
      result = result + "\n " + (Long) row[0] + " " + (Integer) row[1];
    }
    
    result = result + "\n ";
    
    for (Note n : hw.getPagedNotes (2, 10)) {
      result = result + "\n " + n.noteId + " " + n.note;
    }
    
    result = result + "\n\n Criteria API";
    
    for (Note n : hw.getNotesWithCriteriaApi ()) {
      result = result + "\n " + n.noteId + " " + n.note;
    }
    
    
        
    response.getWriter().append(result);
	}

}
