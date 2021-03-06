package com.example;



import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.Metamodel;
import javax.persistence.metamodel.EntityType;
import javax.persistence.TypedQuery;
import javax.persistence.EntityManager;
import javax.persistence.LockModeType;



@Stateless @Remote
public class FilmEJB implements FilmInterface {
  
 
  @PersistenceContext(unitName="something")
  private EntityManager entityManager;
  
  @Override
  public List<Object[]> getNoteToCategory () {
    Query query = entityManager.createQuery 
        ("SELECT n.noteId, n.note, c.id, c.name FROM Note n  JOIN n.categories c");
    @SuppressWarnings("unchecked")
    List<Object[]> rs = (List<Object[]>) query.getResultList();
    
    return rs;    
  }
  
  // inner join n.Project p
  
  @Override
  public List<Object[]> getNotes () {
    Query query = entityManager.createQuery 
        ("SELECT n.noteId, n.note, n.projectID, p.name FROM Note n  INNER JOIN n.project p ORDER BY n.noteId");
   
    @SuppressWarnings("unchecked")
    List<Object[]> rs = (List<Object[]>) query.getResultList();     
    
    return rs;    
  }
  
  /* public List<Project> getProjects () {
    Query query = entityManager.createQuery ("SELECT p FROM Project p inner join p.notes n");
   
    @SuppressWarnings("unchecked")
    List<Project> rs = (List<Project>) query.getResultList();     
    
    return rs;    
  }
  */
  @Override
  public void deleteNote (int id) {
    Note note = entityManager.find(Note.class, id);
    entityManager.remove(entityManager.merge(note));
  }
  
  @Override
  public void updateNote (int id, String text) {
    Note note = entityManager.find (Note.class, id);
    note.setNote(text);
    entityManager.persist(note);
  }
  
  @Override
  public int newNote (String text, int projectId) {
    Note note = new Note();
    note.setNote(text);
    note.setProjectID(projectId);
    entityManager.persist(note);
    entityManager.flush();    
    
    return note.getNoteID();
  }

	@Override
	public void addNoteToCategory (int noteID, int categoryID) {
		Note note = entityManager.find(Note.class, noteID);
		Category category = entityManager.find(Category.class, categoryID);
		NoteToCategory noteToCategory = new NoteToCategory (note, category); 
		entityManager.persist(noteToCategory);
    entityManager.flush();
	}

	@Override
	public void removeNoteToCategory(int noteID, int categoryID) {
		 Query query = entityManager.createQuery 
       ("select n from NoteToCategory n where n.note.noteId = :noteID and n.category.id = :categoryID");
		 query.setParameter("noteID", noteID);
		 query.setParameter("categoryID", categoryID);
		 @SuppressWarnings("unchecked")
		 List<NoteToCategory> rs = (List<NoteToCategory>) query.getResultList();
		 if (!rs.isEmpty()) {
			 // int id = rs.get(0).id;
			 // NoteToCategory noteToCategory = entityManager.find(NoteToCategory.class, id);
			 // entityManager.remove(entityManager.merge(noteToCategory));
			 entityManager.remove(entityManager.merge(rs.get(0)));
		 }		
	}

	
	@Override
	public List<Note> getNoteToCategory2() {
		Query query = entityManager.createQuery("SELECT distinct n FROM Note n  JOIN n.categories c");
    @SuppressWarnings("unchecked")
    List<Note> rs = (List<Note>) query.getResultList();
    
    return rs;
	}

	@Override
	public List<Note> getNotes2() {
		Query query = entityManager.createQuery("SELECT distinct n FROM Note n join n.categories c");//  JOIN n.project p");
		@SuppressWarnings("unchecked")
		List<Note> rs = (List<Note>) query.getResultList();
		
		return rs;
	}
  
	@Override
  public int addAdminUser (String login, int permissionLevel) {
  	AdminUser adminUser = new AdminUser (login, permissionLevel);
  	entityManager.persist(adminUser);
    entityManager.flush();
    
    return adminUser.id;  	
  }

	@Override
	public List<AdminUser> getAdminUsers() {
		
		Query query = entityManager.createQuery("SELECT u FROM AdminUser u");
		@SuppressWarnings("unchecked")
		List<AdminUser> rs = (List<AdminUser>) query.getResultList();
		
		return rs;		
	}

	@Override
	public String updateNoteWithDelay1(int id, String text) {
		String result = "";
		Note note = entityManager.find (Note.class, id, LockModeType.PESSIMISTIC_WRITE);
		Calendar cal = Calendar.getInstance();
    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		result = result + "\n" + "updateNoteWithDelay1: begin " + sdf.format(cal.getTime());
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		note.setNote("updateNoteWithDelay1-");
		entityManager.persist(note);
		Calendar cal2 = Calendar.getInstance();
		result = result + "\n" + "updateNoteWithDelay1: end " + sdf.format(cal2.getTime());
		
		return result;
	}

	@Override
	public String updateNoteWithDelay2(int id, String text) {
		String result = "";
		Note note = entityManager.find (Note.class, id, LockModeType.PESSIMISTIC_WRITE);
		Calendar cal = Calendar.getInstance();
    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		result = result + "\n" + "updateNoteWithDelay2: begin " + sdf.format(cal.getTime());
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			result = result + e.toString();
		}
		note.setNote("updateNoteWithDelay2-");
		result = result + "\n" + updateNoteWithDelay1(id, text);
		
		entityManager.persist(note);
		Calendar cal2 = Calendar.getInstance();
		result = result + "\n" + "updateNoteWithDelay2: end " + sdf.format(cal2.getTime());
		
		return result;
	}

	@Override
	public List<Object[]> getAdminUsersWithGrouping() {
		Query query = entityManager.createQuery
      ("SELECT count (u.id), u.permissionLevel FROM AdminUser u group by u.permissionLevel order by u.permissionLevel");
		
		@SuppressWarnings("unchecked")
    List<Object[]> rs = (List<Object[]>) query.getResultList();
    
    return rs;
	}

	@Override
	public List<Note> getPagedNotes (int pageNumber, int pageSize) {
		
		Query query = entityManager.createQuery("SELECT distinct n FROM Note n order by n.noteId");
		query.setMaxResults (pageSize);
		query.setFirstResult ((pageNumber - 1) * pageSize);
		@SuppressWarnings("unchecked")
		List<Note> rs = (List<Note>) query.getResultList();
		
		return rs;
		
	}

	@Override
	public List<Note> getNotesWithCriteriaApi() {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		Metamodel m = entityManager.getMetamodel();
		EntityType<Note> Note_ = m.entity(Note.class);
		CriteriaQuery<Note> cq = cb.createQuery(Note.class);
		Root<Note> note = cq.from(Note.class);
		cq.where(cb.lt(note.get(Note_.noteId), 90));
		
		cq.select(note);
		TypedQuery<Note> q = entityManager.createQuery(cq);
		List<Note> notes = q.getResultList();
		
		
		return notes;
	}
  

}
