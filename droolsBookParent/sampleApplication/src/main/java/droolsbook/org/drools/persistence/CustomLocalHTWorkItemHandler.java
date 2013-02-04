package droolsbook.org.drools.persistence;

import java.util.HashMap;
import java.util.Map;

import org.drools.runtime.KnowledgeRuntime;
import org.drools.runtime.StatefulKnowledgeSession;
import org.jbpm.eventmessaging.EventResponseHandler;
import org.jbpm.eventmessaging.Payload;
import org.jbpm.process.workitem.wsht.LocalHTWorkItemHandler;
import org.jbpm.task.Content;
import org.jbpm.task.Status;
import org.jbpm.task.Task;
import org.jbpm.task.TaskService;
import org.jbpm.task.event.TaskEventKey;
import org.jbpm.task.event.entity.TaskCompletedEvent;
import org.jbpm.task.event.entity.TaskEvent;
import org.jbpm.task.event.entity.TaskFailedEvent;
import org.jbpm.task.event.entity.TaskSkippedEvent;
import org.jbpm.task.service.responsehandlers.AbstractBaseResponseHandler;
import org.jbpm.task.utils.ContentMarshallerHelper;
import org.jbpm.task.utils.OnErrorAction;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.transaction.support.TransactionTemplate;

import droolsbook.bank.service.impl.LoanApprovalServiceImpl.SessionCleanupTransactionSynchronisation;

public class CustomLocalHTWorkItemHandler extends LocalHTWorkItemHandler {

  private KnowledgeSessionLookup knowledgeSessionLookup;
  
  public CustomLocalHTWorkItemHandler(KnowledgeRuntime session) {
    super(session);
    // TODO Auto-generated constructor stub
  }

  public CustomLocalHTWorkItemHandler(KnowledgeRuntime session,
      boolean owningSessionOnly) {
    super(session, owningSessionOnly);
    // TODO Auto-generated constructor stub
  }

  public CustomLocalHTWorkItemHandler(TaskService client,
      KnowledgeRuntime session, KnowledgeSessionLookup knowledgeSessionLookup) {
    super(client, session);
    this.knowledgeSessionLookup = knowledgeSessionLookup;
  }

  public CustomLocalHTWorkItemHandler(KnowledgeRuntime session,
      OnErrorAction action) {
    super(session, action);
    // TODO Auto-generated constructor stub
  }

  public CustomLocalHTWorkItemHandler(TaskService client,
      KnowledgeRuntime session, boolean owningSessionOnly) {
    super(client, session, owningSessionOnly);
    // TODO Auto-generated constructor stub
  }

  public CustomLocalHTWorkItemHandler(TaskService client,
      KnowledgeRuntime session, OnErrorAction action) {
    super(client, session, action);
    // TODO Auto-generated constructor stub
  }

  public CustomLocalHTWorkItemHandler(TaskService client,
      KnowledgeRuntime session, OnErrorAction action, ClassLoader classLoader) {
    super(client, session, action, classLoader);
    // TODO Auto-generated constructor stub
  }
  
  
  protected void registerTaskEvents() {
    //super.registerTaskEvents();

    CustomTaskCompletedHandler eventResponseHandler = new CustomTaskCompletedHandler();
    TaskEventKey key = new TaskEventKey(TaskCompletedEvent.class, -1);
    getClient().registerForEvent(key, false, eventResponseHandler);
    eventHandlers.put(key, eventResponseHandler);
    key = new TaskEventKey(TaskFailedEvent.class, -1);
    getClient().registerForEvent(key, false, eventResponseHandler);
    eventHandlers.put(key, eventResponseHandler);
    key = new TaskEventKey(TaskSkippedEvent.class, -1);
    getClient().registerForEvent(key, false, eventResponseHandler);
    eventHandlers.put(key, eventResponseHandler);
  }
   
  
  private class CustomTaskCompletedHandler extends AbstractBaseResponseHandler implements EventResponseHandler {

    public void execute(Payload payload) {
        TaskEvent event = (TaskEvent) payload.get();
        final long taskId = event.getTaskId();
        
//        if (owningSessionOnly && (session instanceof StatefulKnowledgeSession)) {
//            if (((StatefulKnowledgeSession) session).getId() != event.getSessionId()) {
//                return;
//            }
//        }
        
        //if (local) {
            handleCompletedTask(taskId);
//        } else {
//            Runnable runnable = new Runnable() {
//
//                public void run() {
//                    handleCompletedTask(taskId);
//                }
//            };
//            new Thread(runnable).start();
//        }
    }

    public boolean isRemove() {
        return false;
    }

    public void handleCompletedTask(long taskId) {
        final Task task = getClient().getTask(taskId);
        final long workItemId = task.getTaskData().getWorkItemId();
        if (task.getTaskData().getStatus() == Status.Completed) {
            String userId = task.getTaskData().getActualOwner().getId();
            final Map<String, Object> results = new HashMap<String, Object>();
            results.put("ActorId", userId);
            long contentId = task.getTaskData().getOutputContentId();
            if (contentId != -1) {
                Content content = getClient().getContent(contentId);
                Object result = ContentMarshallerHelper.unmarshall( content.getContent(), session.getEnvironment(), getClassLoader());
                results.put("Result", result);
                if (result instanceof Map) {
                    Map<?, ?> map = (Map<?, ?>) result;
                    for (Map.Entry<?, ?> entry : map.entrySet()) {
                        if (entry.getKey() instanceof String) {
                            results.put((String) entry.getKey(), entry.getValue());
                        }
                    }
                }

                
//                transactionTemplate.execute(
//                    new TransactionCallbackWithoutResult() {
//                      
//                      @Override
//                      protected void doInTransactionWithoutResult(TransactionStatus status) {
//                        
//                        
//                        StatefulKnowledgeSession session = knowledgeSessionLookup.loadSession(task.getTaskData().getProcessSessionId());
//                        try {
//                          session.getWorkItemManager().completeWorkItem(
//                              workItemId, results );
//                        } finally {
//                          TransactionSynchronizationManager.registerSynchronization(new SessionCleanupTransactionSynchronisation(session, "handleCompletedTask1"));
//                        }
//                        
//                        
//                      }
//                    });
                
                session.getWorkItemManager().completeWorkItem(task.getTaskData().getWorkItemId(), results);
            } else {
              
                      StatefulKnowledgeSession session = knowledgeSessionLookup.loadSession(task.getTaskData().getProcessSessionId());
                      try {
                        session.getWorkItemManager().completeWorkItem(
                            workItemId, results );
                      } finally {
                        TransactionSynchronizationManager.registerSynchronization(new SessionCleanupTransactionSynchronisation(session, "handleCompletedTask2"));
                        try {
                          dispose();
                        } catch (Exception e) {
                          e.printStackTrace();
                        }
                      }
                      
            }
        } else {
          session.getWorkItemManager().abortWorkItem(workItemId);
        }
    }
}

}
