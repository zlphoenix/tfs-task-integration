/*
 * Copyright 2000-2013 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package task;

import com.intellij.openapi.application.PathManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.tasks.*;
import com.intellij.tasks.impl.BaseRepository;
import com.intellij.tasks.impl.BaseRepositoryImpl;
import com.intellij.util.xmlb.annotations.Tag;
import com.microsoft.tfs.core.TFSTeamProjectCollection;
import com.microsoft.tfs.core.clients.workitem.WorkItem;
import com.microsoft.tfs.core.clients.workitem.WorkItemClient;
import com.microsoft.tfs.core.clients.workitem.query.Query;
import com.microsoft.tfs.core.clients.workitem.query.WorkItemCollection;
import com.microsoft.tfs.core.httpclient.Credentials;
import com.microsoft.tfs.core.httpclient.UsernamePasswordCredentials;
import icons.TFSIcons;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Pattern;

/**
 * @author Dennis.Ushakov
 */
@Tag("TFS")
public class TFSRepository extends BaseRepositoryImpl {
  private static final Logger LOG = Logger.getInstance("#task.TFSTaskRepository");
  private static final Pattern DATE_PATTERN = Pattern.compile("(\\d\\d\\d\\d\\-\\d\\d\\-\\d\\d).*(\\d\\d:\\d\\d:\\d\\d).*");
  private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


  /** for serialization */
  @SuppressWarnings({"UnusedDeclaration"})
  public TFSRepository() {
  }

  public TFSRepository(TaskRepositoryType type) {
    super(type);

  }

  @Override
  public BaseRepository clone() {
    return new TFSRepository(this);
  }

  private TFSRepository(TFSRepository other) {
    super(other);
    setUsername(other.myUsername);
    setPassword(other.myPassword);

  }

  @Override
  public void testConnection() throws Exception {
    getIssues(null, 10, 0);
  }

  @Override
  public boolean isConfigured() {
    return super.isConfigured();
  }

  @Override
  public Task[] getIssues(@Nullable String query, int max, long since) throws Exception {
    TFSTeamProjectCollection tpc;
    LOG.warn("getIssues");



    Credentials credentials = new UsernamePasswordCredentials(myUsername,myPassword);
    URL uri = new URL(getUrl());
    tpc = new TFSTeamProjectCollection(uri.toURI(), credentials);
    tpc.authenticate();

    LOG.warn("getIssues2");
    try{
      WorkItemClient workItemClient = tpc.getWorkItemClient();
      String wiqlQuery = "Select ID, Title, Description from WorkItems where (State = 'Active') AND (System.AssignedTo = '"+workItemClient.getUserDisplayName()+"')";
      LOG.warn(wiqlQuery);
      Query query2 = workItemClient.createQuery(wiqlQuery);
      WorkItemCollection workItemCollection = query2.runQuery();

      ArrayList<Task> tasks = new ArrayList<Task>();
      for(int i = 0; i < workItemCollection.size();i++){

        WorkItem w = workItemCollection.getWorkItem(i);
        LOG.warn(w.toString());
        tasks.add(createTask(Integer.toString(w.getID()),w.getTitle(),(String)w.getFields().getField("Description").getValue()));
      }
      return tasks.toArray(new Task[tasks.size()]);
    }catch(Exception e){
      e.printStackTrace();
    }

    return null;
  }

  @Nullable
  private Task createTask(final String ID, final String Title, final String Description) {

    return new Task() {
      @Override
      public boolean isIssue() {
        return true;
      }

      @Nullable
      @Override
      public String getIssueUrl() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
      }


      @NotNull
      @Override
      public String getId() {
        return ID;
      }

      @NotNull
      @Override
      public String getSummary() {
        return Title;
      }

      public String getDescription() {
        return Description;
      }

      @NotNull
      @Override
      public Comment[] getComments() {
        return new Comment[0];
      }

      @NotNull
      @Override
      public Icon getIcon() {
        return TFSIcons.TFSIcon;
      }

      @NotNull
      @Override
      public TaskType getType() {
        return TaskType.OTHER;
      }

      @Nullable
      @Override
      public Date getUpdated() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
      }

      @Nullable
      @Override
      public Date getCreated() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
      }

      @Override
      public boolean isClosed() {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
      }


      @Override
      public TaskRepository getRepository() {
        return TFSRepository.this;
      }

      @Override
      public String getPresentableName() {
        return getId() + ": " + getSummary();
      }
    };
  }

  @Nullable
  @Override
  public Task findTask(String id) throws Exception {
    return null;
  }



  @Override
  public boolean equals(Object o) {
    if (!super.equals(o)) return false;
    if (!(o instanceof TFSRepository)) return false;

    TFSRepository that = (TFSRepository)o;
    return true;
  }
}
