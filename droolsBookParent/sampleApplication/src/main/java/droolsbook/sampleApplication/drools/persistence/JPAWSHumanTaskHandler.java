package droolsbook.sampleApplication.drools.persistence;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.mina.transport.socket.nio.NioSocketConnector;
import org.drools.SystemEventListenerFactory;
import org.drools.eventmessaging.EventKey;
import org.drools.eventmessaging.EventResponseHandler;
import org.drools.eventmessaging.Payload;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.process.WorkItem;
import org.drools.runtime.process.WorkItemHandler;
import org.drools.runtime.process.WorkItemManager;
import org.drools.task.AccessType;
import org.drools.task.Content;
import org.drools.task.Group;
import org.drools.task.I18NText;
import org.drools.task.OrganizationalEntity;
import org.drools.task.PeopleAssignments;
import org.drools.task.SubTasksStrategy;
import org.drools.task.SubTasksStrategyFactory;
import org.drools.task.Task;
import org.drools.task.TaskData;
import org.drools.task.User;
import org.drools.task.event.TaskCompletedEvent;
import org.drools.task.event.TaskEvent;
import org.drools.task.event.TaskEventKey;
import org.drools.task.event.TaskFailedEvent;
import org.drools.task.event.TaskSkippedEvent;
import org.drools.task.service.ContentData;
import org.drools.task.service.TaskClient;
import org.drools.task.service.TaskClientHandler;
import org.drools.task.service.TaskClientHandler.AddTaskResponseHandler;
import org.drools.task.service.TaskClientHandler.GetContentResponseHandler;
import org.drools.task.service.TaskClientHandler.GetTaskResponseHandler;
import org.drools.task.service.mina.MinaTaskClientConnector;
import org.drools.task.service.mina.MinaTaskClientHandler;
import org.drools.task.service.responsehandlers.AbstractBaseResponseHandler;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import droolsbook.org.drools.persistence.KnowledgeSessionLookup;

public class JPAWSHumanTaskHandler implements WorkItemHandler {

  private KnowledgeSessionLookup knowledgeSessionLookup;  
  private TransactionTemplate transactionTemplate;  
  private Map<Long, Integer> processIdToSessionIdMap = 
    Collections.synchronizedMap(new HashMap<Long, Integer>());
  
  private String ipAddress = "127.0.0.1";
  private int port = 9123;
  private TaskClient client;
  private Map<Long, WorkItemManager> managers = new HashMap<Long, WorkItemManager>();
  private Map<Long, Long> idMapping = new HashMap<Long, Long>();

  public void addSessionId(Long processId, Integer sessionId) {
    processIdToSessionIdMap.put(processId, sessionId);
  }
  
  public Map<Long, Integer> getProcessIdToSessionIdMap() {
    return processIdToSessionIdMap;
  }
  
  public void setConnection(String ipAddress, int port) {
    this.ipAddress = ipAddress;
    this.port = port;
  }
  
  public void connect() {
    if (client == null) {
    	TaskClient client = new TaskClient(new MinaTaskClientConnector("org.drools.process.workitem.wsht.WSHumanTaskHandler",
        		new MinaTaskClientHandler(SystemEventListenerFactory.getSystemEventListener())));
      boolean connected = client.connect(ipAddress, port);
      if (!connected) {
        throw new IllegalArgumentException(
          "Could not connect task client");
      }
    }
  }

  public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
    connect();
    Task task = new Task();
    String taskName = (String) workItem.getParameter("TaskName");
    if (taskName != null) {
      List<I18NText> names = new ArrayList<I18NText>();
      names.add(new I18NText("en-UK", taskName));
      task.setNames(names);
    }
    String comment = (String) workItem.getParameter("Comment");
    if (comment != null) {
      List<I18NText> descriptions = new ArrayList<I18NText>();
      descriptions.add(new I18NText("en-UK", comment));
      task.setDescriptions(descriptions);
      List<I18NText> subjects = new ArrayList<I18NText>();
      subjects.add(new I18NText("en-UK", comment));
      task.setSubjects(subjects);
    }
    String priorityString = (String) workItem.getParameter("Priority");
    int priority = 0;
    if (priorityString != null) {
      try {
        priority = new Integer(priorityString);
      } catch (NumberFormatException e) {
        // do nothing
      }
    }
    task.setPriority(priority);
    
    TaskData taskData = new TaskData();
    taskData.setWorkItemId(workItem.getId());
    taskData.setSkipable(!"false".equals(workItem.getParameter("Skippable")));
        //Sub Task Data
        Long parentId = (Long) workItem.getParameter("ParentId");
        if(parentId != null){
            taskData.setParentId(parentId);
        }

        String subTaskStrategiesCommaSeparated = (String) workItem.getParameter("SubTaskStrategies");
        if(subTaskStrategiesCommaSeparated!= null && !subTaskStrategiesCommaSeparated.equals("")){
            String[] subTaskStrategies =  subTaskStrategiesCommaSeparated.split(",");
            List<SubTasksStrategy> strategies = new ArrayList<SubTasksStrategy>();
            for(String subTaskStrategyString : subTaskStrategies){
                SubTasksStrategy subTaskStrategy = SubTasksStrategyFactory.newStrategy(subTaskStrategyString);
                strategies.add(subTaskStrategy);
            }
            task.setSubTaskStrategies(strategies);
        }

        PeopleAssignments assignments = new PeopleAssignments();
    List<OrganizationalEntity> potentialOwners = new ArrayList<OrganizationalEntity>();

        String actorId = (String) workItem.getParameter("ActorId");
    if (actorId != null) {
      
      String[] actorIds = actorId.split(",");
      for (String id: actorIds) {
        User user = new User();
        user.setId(id.trim());
        potentialOwners.add(user);
      }
            //Set the first user as creator ID??? hmmm might be wrong
            if (potentialOwners.size() > 0){
                taskData.setCreatedBy((User)potentialOwners.get(0));
            }
        }
        String groupId = (String) workItem.getParameter("GroupId");
    if (groupId != null) {
      
      String[] groupIds = groupId.split(",");
      for (String id: groupIds) {

        potentialOwners.add(new Group(id));
      }
      
    }

        assignments.setPotentialOwners(potentialOwners);
    List<OrganizationalEntity> businessAdministrators = new ArrayList<OrganizationalEntity>();
    businessAdministrators.add(new User("Administrator"));
    assignments.setBusinessAdministrators(businessAdministrators);
    task.setPeopleAssignments(assignments);
        
    task.setTaskData(taskData);

    ContentData content = null;
    Object contentObject = workItem.getParameter("Content");
    if (contentObject != null) {
      ByteArrayOutputStream bos = new ByteArrayOutputStream();
      ObjectOutputStream out;
      try {
        out = new ObjectOutputStream(bos);
        out.writeObject(contentObject);
        out.close();
        content = new ContentData();
        content.setContent(bos.toByteArray());
        content.setAccessType(AccessType.Inline);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    TaskWorkItemAddTaskResponseHandler taskResponseHandler =
      new TaskWorkItemAddTaskResponseHandler(this, this.client, this.managers,
        this.idMapping, manager, workItem);
    client.addTask(task, content, taskResponseHandler);
  }
  
  public void dispose() {
    if (client != null) {
      client.disconnect();
    }
  }

  public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
    Long taskId = idMapping.get(workItem.getId());
    if (taskId != null) {
      synchronized (idMapping) {
        idMapping.remove(taskId);
      }
      synchronized (managers) {
        managers.remove(taskId);
      }
      client.skip(taskId, "Administrator", null);
    }
  }
  
  private static void doCompleteWorkItem(
      final JPAWSHumanTaskHandler handler,
      final WorkItem workItem, final long workItemId,
      final Map<String, Object> results) {
    // @extract-start 08 12        
    handler.getTransactionTemplate().execute(
        new TransactionCallback() {
      public Object doInTransaction(TransactionStatus status) {
        Integer sessionId = handler.getProcessIdToSessionIdMap()
          .get(workItem.getProcessInstanceId());
        StatefulKnowledgeSession session = handler
          .getKnowledgeSessionLookup().loadSession(sessionId);
        try {
          session.getWorkItemManager().completeWorkItem(
              workItemId, results );
        } finally {
          session.dispose();
        }
        return null;
      }
    });
    // @extract-end
   }
  
  private static void doAbortWorkItem(
      final JPAWSHumanTaskHandler handler,
      final WorkItem workItem, final long workItemId) {
    handler.getTransactionTemplate().execute(
        new TransactionCallback() {
      public Object doInTransaction(TransactionStatus status) {
        Integer sessionId = handler.getProcessIdToSessionIdMap()
          .get(workItem.getProcessInstanceId());
        StatefulKnowledgeSession session = handler
          .getKnowledgeSessionLookup().loadSession(sessionId);
        try {
          session.getWorkItemManager().abortWorkItem(
              workItemId);
        } finally {
          session.dispose();
        }
        return null;
      }
    });
   }
  
  public void setKnowledgeSessionLookup(
      KnowledgeSessionLookup knowledgeSessionLookup) {
    this.knowledgeSessionLookup = knowledgeSessionLookup;
  }
  
  public KnowledgeSessionLookup getKnowledgeSessionLookup() {
    return knowledgeSessionLookup;
  }
  
  public void setTransactionTemplate(
      TransactionTemplate transactionTemplate) {
    this.transactionTemplate = transactionTemplate;
  }
  
  public TransactionTemplate getTransactionTemplate() {
    return transactionTemplate;
  }
  
    public static class TaskWorkItemAddTaskResponseHandler extends AbstractBaseResponseHandler implements AddTaskResponseHandler {
        private Map<Long, WorkItemManager> managers;
        private Map<Long, Long> idMapping;
        private WorkItemManager manager;
        private WorkItem workItem;
        private MinaTaskClient client;
        private JPAWSHumanTaskHandler handler;
        
        public TaskWorkItemAddTaskResponseHandler(JPAWSHumanTaskHandler handler, MinaTaskClient client,
            Map<Long, WorkItemManager> managers,  Map<Long, Long> idMapping,
            WorkItemManager manager, WorkItem workItem) {
            this.handler = handler;
            this.client = client;
            this.managers = managers;
            this.idMapping = idMapping;
            this.manager = manager;
            this.workItem = workItem;
        }
        
        public void execute(long taskId) {
          synchronized ( managers ) {
                managers.put(taskId, this.manager);           
            }
            synchronized ( idMapping ) {
                idMapping.put(workItem.getId(), taskId);           
            }
            EventKey key = new TaskEventKey(TaskCompletedEvent.class, taskId );           
            TaskCompletedHandler eventResponseHandler =
              new TaskCompletedHandler(handler, workItem, taskId, managers, client); 
            client.registerForEvent( key, true, eventResponseHandler );
            key = new TaskEventKey(TaskFailedEvent.class, taskId );           
            client.registerForEvent( key, true, eventResponseHandler );
            key = new TaskEventKey(TaskSkippedEvent.class, taskId );           
            client.registerForEvent( key, true, eventResponseHandler );
        }
    }
    
    private static class TaskCompletedHandler extends AbstractBaseResponseHandler implements EventResponseHandler {
        private WorkItem workItem;
        private long taskId;
        private Map<Long, WorkItemManager> managers;
        private MinaTaskClient client;
        private JPAWSHumanTaskHandler handler;
        
        public TaskCompletedHandler(JPAWSHumanTaskHandler handler, WorkItem workItem, long taskId, Map<Long, WorkItemManager> managers,
            MinaTaskClient client) {
            this.workItem = workItem;
            this.taskId = taskId;
            this.managers = managers;
            this.client = client;
            this.handler = handler;
        }

        public void execute(Payload payload) {
            TaskEvent event = ( TaskEvent ) payload.get();
          if ( event.getTaskId() != taskId ) {
                // defensive check that should never happen, just here for testing                
                setError(new IllegalStateException("Expected task id and arrived task id do not march"));
                return;
            }
          if (event instanceof TaskCompletedEvent) {
            synchronized ( this.managers ) {
                WorkItemManager manager = this.managers.get(taskId);
                if (manager != null) {
                  GetTaskResponseHandler getTaskResponseHandler =
                    new GetCompletedTaskResponseHandler(handler, manager, client, workItem);
                  client.getTask(taskId, getTaskResponseHandler);   
                }
            }
          } else {
            synchronized ( this.managers ) {
              WorkItemManager manager = this.managers.get(taskId);
                if (manager != null) {
                  //manager.abortWorkItem(workItem.getId());
                  doAbortWorkItem(handler, workItem, workItem.getId());
                }
            }
          }
        }
    }
    
    private static class GetCompletedTaskResponseHandler extends AbstractBaseResponseHandler implements GetTaskResponseHandler {

      private WorkItemManager manager;
      private MinaTaskClient client;
      private JPAWSHumanTaskHandler handler;
      private WorkItem workItem;
      
      public GetCompletedTaskResponseHandler(JPAWSHumanTaskHandler handler, WorkItemManager manager, MinaTaskClient client, WorkItem workItem) {
        this.manager = manager;
        this.client = client;
        this.handler = handler;
        this.workItem = workItem;
      }
      
    public void execute(Task task) {
      final long workItemId = task.getTaskData().getWorkItemId();
      String userId = task.getTaskData().getActualOwner().getId();
      final Map<String, Object> results = new HashMap<String, Object>();
      results.put("ActorId", userId);
      long contentId = task.getTaskData().getOutputContentId();
      if (contentId != -1) {
        GetContentResponseHandler getContentResponseHandler =
          new GetResultContentResponseHandler(handler, manager, task, results, workItem);
        client.getContent(contentId, getContentResponseHandler);
      } else {
        doCompleteWorkItem(handler, workItem, workItemId, results);        
        //manager.completeWorkItem(workItemId, results);
      }
     }
    }
    
    private static class GetResultContentResponseHandler extends AbstractBaseResponseHandler implements GetContentResponseHandler {

      private WorkItemManager manager;
      private Task task;
      private Map<String, Object> results;
      private JPAWSHumanTaskHandler handler;
      private WorkItem workItem;

      public GetResultContentResponseHandler(JPAWSHumanTaskHandler handler, WorkItemManager manager, Task task, Map<String, Object> results, WorkItem workItem) {
        this.manager = manager;
        this.task = task;
        this.results = results;
        this.handler = handler;
        this.workItem = workItem;
      }
      
    public void execute(Content content) {
      ByteArrayInputStream bis = new ByteArrayInputStream(content.getContent());
      ObjectInputStream in;
      try {
        in = new ObjectInputStream(bis);
        Object result = in.readObject();
        in.close();
        results.put("Result", result);
        //manager.completeWorkItem(task.getTaskData().getWorkItemId(), results);
        doCompleteWorkItem(handler, workItem, task.getTaskData().getWorkItemId(), results); 
      } catch (IOException e) {
        e.printStackTrace();
      } catch (ClassNotFoundException e) {
        e.printStackTrace();
      }
    }
    }
}

