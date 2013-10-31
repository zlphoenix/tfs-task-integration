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
import com.intellij.openapi.project.Project;
import com.intellij.tasks.TaskRepository;
import com.intellij.tasks.TaskState;
import com.intellij.tasks.config.BaseRepositoryEditor;
import com.intellij.tasks.config.TaskRepositoryEditor;
import com.intellij.tasks.impl.BaseRepositoryType;
import com.intellij.util.Consumer;
import icons.TFSIcons;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.EnumSet;

/**
 * Created with IntelliJ IDEA.
 * User: david
 * Date: 15.10.13
 * Time: 17:16
 * To change this template use File | Settings | File Templates.
 */
public class TFSRepositoryType extends BaseRepositoryType<TFSRepository> {

  TFSRepositoryType(){
    System.setProperty("com.microsoft.tfs.jni.native.base-directory", PathManager.getLibPath()+"/native");
    System.out.println( PathManager.getLibPath()+"/native");
  }

  @NotNull
  @Override
  public String getName() {
    return "TFS";
  }

  @NotNull
  @Override
  public Icon getIcon() {
    return TFSIcons.TFSIcon;
  }

  @NotNull
  @Override
  public TaskRepository createRepository() {
    return new TFSRepository(this);
  }

  @Override
  public Class<TFSRepository> getRepositoryClass() {
    return TFSRepository.class;
  }

  @Override
  public EnumSet<TaskState> getPossibleTaskStates() {
    return EnumSet.of(TaskState.SUBMITTED, TaskState.OPEN, TaskState.RESOLVED, TaskState.OTHER);
  }

  @NotNull
  @Override
  public TaskRepositoryEditor createEditor(TFSRepository repository,
                                           Project project,
                                           Consumer<TFSRepository> changeListener) {
    return new BaseRepositoryEditor(project, repository, changeListener);
  }
}
