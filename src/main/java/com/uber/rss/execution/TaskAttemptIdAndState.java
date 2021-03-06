/*
 * Copyright (c) 2020 Uber Technologies, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.uber.rss.execution;

import com.uber.rss.common.AppMapId;
import com.uber.rss.exceptions.RssInvalidStateException;

/**
 * This class stores state for a task attempt.
 */
public class TaskAttemptIdAndState {
  private enum TaskAttemptState {
    NOT_STARTED,
    START_UPLOAD,
    FINISH_UPLOAD,
    COMMITTED
  }
  
  private AppMapId appMapId;
  private long taskAttemptId;
  private TaskAttemptState state = TaskAttemptState.NOT_STARTED;

  public TaskAttemptIdAndState(AppMapId appMapId, long taskAttemptId) {
    this.appMapId = appMapId;
    this.taskAttemptId = taskAttemptId;
  }

  public AppMapId getAppMapId() {
    return appMapId;
  }

  public long getTaskAttemptId() {
    return taskAttemptId;
  }

  public void markStartUpload() {
    TaskAttemptState targetState = TaskAttemptState.START_UPLOAD;
    if (state != TaskAttemptState.NOT_STARTED) {
      throw new RssInvalidStateException(String.format(
          "Cannot mark attempt to state %s from its current state %s, %s, %s", targetState, state, appMapId, taskAttemptId));
    }
    state = targetState;
  }

  public void markFinishUpload() {
    TaskAttemptState targetState = TaskAttemptState.FINISH_UPLOAD;
    if (state != TaskAttemptState.START_UPLOAD) {
      throw new RssInvalidStateException(String.format(
          "Cannot mark attempt to state %s from its current state %s, %s, %s", targetState, state, appMapId, taskAttemptId));
    }
    state = targetState;
  }

  public void markCommitted() {
    TaskAttemptState targetState = TaskAttemptState.COMMITTED;
    if (state != TaskAttemptState.NOT_STARTED && state != TaskAttemptState.FINISH_UPLOAD && state != TaskAttemptState.COMMITTED) {
      throw new RssInvalidStateException(String.format(
          "Cannot mark attempt to state %s from its current state %s, %s, %s", targetState, state, appMapId, taskAttemptId));
    }
    state = targetState;
  }

  public boolean isCommitted() {
    return state == TaskAttemptState.COMMITTED;
  }

  public boolean isFinishedUpload() {
    return state == TaskAttemptState.FINISH_UPLOAD;
  }

  @Override
  public String toString() {
    return "TaskAttemptIdAndState{" +
        "appMapId=" + appMapId +
        ", taskAttemptId=" + taskAttemptId +
        ", state=" + state +
        '}';
  }
}
