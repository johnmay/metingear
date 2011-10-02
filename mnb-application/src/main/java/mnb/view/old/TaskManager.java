/*
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the Lesser GNU General Public License as published
 *  by the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>
 */
package mnb.view.old;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.swing.SwingUtilities;
import uk.ac.ebi.metabolomes.run.RunnableTask;
import uk.ac.ebi.metabolomes.run.TaskStatus;
import uk.ac.ebi.mnb.main.MainFrame;

/**
 *
 * @author johnmay
 * @date   Apr 28, 2011
 */
public class TaskManager
        implements Runnable {

    private static final org.apache.log4j.Logger logger =
            org.apache.log4j.Logger.getLogger(TaskManager.class);
    private int MAX_TASKS = 2;
    private ArrayList<RunnableTask> queuedTasks;
    private ArrayList<RunnableTask> runningTasks;
    private ArrayList<RunnableTask> completedTasks;

    private TaskManager() {
        super();
        queuedTasks = new ArrayList<RunnableTask>();
        completedTasks = new ArrayList<RunnableTask>();
        runningTasks = new ArrayList<RunnableTask>();
    }

    public static TaskManager getInstance() {
        return TaskManagerHolder.INSTANCE;
    }

    public ArrayList<RunnableTask> getQueuedTasks() {
        return queuedTasks;
    }

    public ArrayList<RunnableTask> getRunningTasks() {
        return runningTasks;
    }

    public ArrayList<RunnableTask> getCompletedTasks() {
        return completedTasks;
    }

    public List<RunnableTask> getTasks() {
        List<RunnableTask> tasks = new ArrayList();

        tasks.addAll(getQueuedTasks());
        tasks.addAll(getRunningTasks());
        tasks.addAll(getCompletedTasks());

        return tasks;

    }

    /**
     * Adds a single tasks to the queue
     * @param tasks
     * @return
     */
    public boolean add(RunnableTask task) {
        return queuedTasks.add(task);
    }

    /**
     * Adds all tasks to the queue
     * @param tasks
     * @return
     */
    public boolean addAll(Collection<? extends RunnableTask> tasks) {
        return queuedTasks.addAll(tasks);
    }

    /**
     * Adds one or more tasks to the queue
     * @param task
     * @return
     */
    public boolean add(RunnableTask... task) {
        return queuedTasks.addAll(Arrays.asList(task));
    }

    // run tasks
    public void run() {

        executor = Executors.newSingleThreadScheduledExecutor();
        executor.scheduleAtFixedRate(getUpdateThead(), 0, 1, TimeUnit.SECONDS);

        int currentQueuePosition = 0;

        // run the tasks while there are queued tasks
        do {

            while (queuedTasks.size() > 0 && runningTasks.size() < MAX_TASKS) {
                if (currentQueuePosition < queuedTasks.size()) {
                    RunnableTask task = queuedTasks.get(currentQueuePosition++);
                    queuedTasks.remove(task);
                    runningTasks.add(task);
                    task.getRunnableThread().start();
                }
            }


            // only check task management every second otherwise to restrain CPU useage
            try {
                Thread.sleep(1000L);
            } catch (InterruptedException ex) {
                logger.error("TaskManager Interrupted!");
            }


            for (int i = 0; i < runningTasks.size(); i++) {
                RunnableTask task = runningTasks.get(i);
                if (task.getStatus() == TaskStatus.COMPLETED
                        || task.getStatus() == TaskStatus.ERROR) {
                    runningTasks.remove(task);
                    completedTasks.add(task);
                    if (task.isCompleted()) {
                        task.postrun();
                    }
                }
            }

            MainFrame.getInstance().repaint();

        } while (queuedTasks.size() > 0
                || runningTasks.size() > 0);

        executor.shutdown();
        SwingUtilities.invokeLater(getUpdateThead());

    }

    public Runnable getUpdateThead() {
        return new Runnable() {

            public void run() {
                SwingUtilities.invokeLater(
                        new Runnable() {
                            public void run() {
                                MainFrame.getInstance().update();
                            }
                        });
            }
        };
    }

    public int getMaxSimultaneousJobs() {
        return MAX_TASKS;
    }

    public void setMaxSimultaneousJobs(int maxSimultaneousJobs) {
        this.MAX_TASKS = maxSimultaneousJobs;



    }

    private static class TaskManagerHolder {

        private static final TaskManager INSTANCE = new TaskManager();
    }
    ScheduledExecutorService executor;
}
