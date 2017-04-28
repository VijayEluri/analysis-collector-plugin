package hudson.plugins.analysis.collector.dashboard;

import java.util.Collection;

import org.kohsuke.stapler.DataBoundConstructor;

import hudson.Extension;

import hudson.model.Descriptor;
import hudson.model.Job;

import hudson.plugins.analysis.collector.AnalysisDescriptor;
import hudson.plugins.analysis.collector.AnalysisProjectAction;
import hudson.plugins.analysis.collector.Messages;
import hudson.plugins.analysis.collector.WarningsAggregator;
import hudson.plugins.analysis.core.AbstractProjectAction;
import hudson.plugins.analysis.dashboard.AbstractWarningsTablePortlet;
import hudson.plugins.view.dashboard.DashboardPortlet;

/**
 * A portlet that shows a table with the number of warnings in a job.
 *
 * @author Ulli Hafner
 */
public class WarningsTablePortlet extends AbstractWarningsTablePortlet {
    /** Determines whether images should be used in the table header. */
    private final boolean useImages;

   /**
     * Aggregates the warnings in participating analysis plug-ins.
     *
     * @since 1.37
     */
    private WarningsAggregator warningsAggregator;

    /**
     * Creates a new instance of {@link WarningsTablePortlet}.
     *
     * @param name
     *            the name of the portlet
     * @param useImages
     *            determines whether images should be used in the table header.
     * @param checkStyleActivated
     *            determines whether to show the warnings from Checkstyle
     * @param dryActivated
     *            determines whether to show the warnings from DRY
     * @param findBugsActivated
     *            determines whether to show the warnings from FindBugs
     * @param pmdActivated
     *            determines whether to show the warnings from PMD
     * @param openTasksActivated
     *            determines whether to show open tasks
     * @param warningsActivated
     *            determines whether to show compiler warnings
     * @param canHideZeroWarningsProjects
     *            determines if zero warnings projects should be hidden in the table
     */
    @DataBoundConstructor
    // CHECKSTYLE:OFF
    public WarningsTablePortlet(final String name, final boolean useImages, final boolean checkStyleActivated,
            final boolean dryActivated, final boolean findBugsActivated, final boolean pmdActivated,
            final boolean openTasksActivated, final boolean warningsActivated,
            final boolean androidLintActivated, final boolean canHideZeroWarningsProjects) {
        // CHECKSTYLE:ON
        super(name, canHideZeroWarningsProjects);

        this.useImages = useImages;

        warningsAggregator = new WarningsAggregator(checkStyleActivated, dryActivated, findBugsActivated,
                pmdActivated, openTasksActivated, warningsActivated, androidLintActivated);
    }

    /**
     * Upgrade for release 1.36 or older.
     *
     * @return this
     */
    @SuppressWarnings("deprecation")
    protected Object readResolve() {
        if (warningsAggregator == null) {
            warningsAggregator = new WarningsAggregator(!isCheckStyleDeactivated, !isDryDeactivated,
                    !isFindBugsDeactivated, !isPmdDeactivated, !isOpenTasksDeactivated, !isWarningsDeactivated, !isAndroidLintDeactivated);
        }
        return this;
    }

    @Override
    protected Class<? extends AbstractProjectAction<?>> getAction() {
        return AnalysisProjectAction.class;
    }

    /**
     * Returns whether images should be used in the table header.
     *
     * @return <code>true</code> if images should be used, <code>false</code> if
     *         text should be used
     */
    public boolean getUseImages() {
        return useImages;
    }

    /**
     * Returns whether icons should be used in the table header.
     *
     * @return <code>true</code> if icons should be used, <code>false</code> if
     *         text should be used
     */
    public boolean useIcons() {
        return useImages;
    }

    /**
     * Returns whether the totals column should be shown.
     *
     * @return <code>true</code> if the totals column should be shown, <code>false</code> otherwise
     */
    public boolean isTotalsVisible() {
        return toInt(isCheckStyleActivated())
                + toInt(isDryActivated())
                + toInt(isFindBugsActivated())
                + toInt(isPmdActivated())
                + toInt(isOpenTasksActivated())
                + toInt(isWarningsActivated())
                + toInt(isAndroidLintActivated()) > 1;
    }

    private int toInt(final boolean isActivated) {
        return isActivated ? 1 : 0;
    }

    /**
     * Returns whether CheckStyle results should be shown.
     *
     * @return <code>true</code> if CheckStyle results should be shown, <code>false</code> otherwise
     */
    public boolean isCheckStyleActivated() {
        return warningsAggregator.isCheckStyleActivated();
    }

    /**
     * Returns whether DRY results should be shown.
     *
     * @return <code>true</code> if DRY results should be shown, <code>false</code> otherwise
     */
    public boolean isDryActivated() {
        return warningsAggregator.isDryActivated();
    }

    /**
     * Returns whether FindBugs results should be shown.
     *
     * @return <code>true</code> if FindBugs results should be shown, <code>false</code> otherwise
     */
    public boolean isFindBugsActivated() {
        return warningsAggregator.isFindBugsActivated();
    }

    /**
     * Returns whether PMD results should be shown.
     *
     * @return <code>true</code> if PMD results should be shown, <code>false</code> otherwise
     */
    public boolean isPmdActivated() {
        return warningsAggregator.isPmdActivated();
    }

    /**
     * Returns whether open tasks should be shown.
     *
     * @return <code>true</code> if open tasks should be shown, <code>false</code> otherwise
     */
    public boolean isOpenTasksActivated() {
        return warningsAggregator.isOpenTasksActivated();
    }

    /**
     * Returns whether compiler warnings results should be shown.
     *
     * @return <code>true</code> if compiler warnings results should be shown, <code>false</code> otherwise
     */
    public boolean isWarningsActivated() {
        return warningsAggregator.isWarningsActivated();
    }

    /**
     * Returns whether Android lint results should be shown.
     *
     * @return <code>true</code> if Android lint results should be shown, <code>false</code> otherwise
     */
    public boolean isAndroidLintActivated() {
        return warningsAggregator.isAndroidLintActivated();
    }

    @Override
    protected boolean isVisibleJob(final Job<?, ?> job) {
        return toInt(getTotal(job)) > 0;
    }

    /**
     * Returns the number of warnings for the specified job.
     *
     * @param job
     *            the job to get the warnings for
     * @return the number of warnings
     */
    public String getTotal(final Job<?, ?> job) {
        return warningsAggregator.getTotal(job);
    }

    /**
     * Returns the number of Checkstyle warnings for the specified job.
     *
     * @param job
     *            the job to get the warnings for
     * @return the number of Checkstyle warnings
     */
    public String getCheckStyle(final Job<?, ?> job) {
        return warningsAggregator.getCheckStyle(job);
    }

    /**
     * Returns the number of duplicate code warnings for the specified job.
     *
     * @param job
     *            the job to get the warnings for
     * @return the number of duplicate code warnings
     */
    public String getDry(final Job<?, ?> job) {
        return warningsAggregator.getDry(job);
    }

    /**
     * Returns the number of FindBugs warnings for the specified job.
     *
     * @param job
     *            the job to get the warnings for
     * @return the number of FindBugs warnings
     */
    public String getFindBugs(final Job<?, ?> job) {
        return warningsAggregator.getFindBugs(job);
    }

    /**
     * Returns the number of PMD warnings for the specified job.
     *
     * @param job
     *            the job to get the warnings for
     * @return the number of PMD warnings
     */
    public String getPmd(final Job<?, ?> job) {
        return warningsAggregator.getPmd(job);
    }

    /**
     * Returns the number of open tasks for the specified job.
     *
     * @param job
     *            the job to get the tasks for
     * @return the number of open tasks
     */
    public String getTasks(final Job<?, ?> job) {
        return warningsAggregator.getTasks(job);
    }

    /**
     * Returns the total number of warnings for the specified job.
     *
     * @param job
     *            the job to get the warnings for
     * @return the number of compiler warnings
     */
    public String getCompilerWarnings(final Job<?, ?> job) {
        return warningsAggregator.getCompilerWarnings(job);
    }

    /**
     * Returns the total number of Android lint warnings for the specified job.
     *
     * @param job
     *            the job to get the warnings for
     * @return the number of Android lint warnings
     */
    public String getAndroidLint(final Job<?, ?> job) {
        return warningsAggregator.getAndroidLint(job);
    }

    /**
     * Returns the number of Checkstyle warnings for the specified jobs.
     *
     * @param jobs
     *            the jobs to get the warnings for
     * @return the number of Checkstyle warnings
     */
    public String getCheckStyle(final Collection<Job<?, ?>> jobs) {
        int sum = 0;
        for (Job<?, ?> job : jobs) {
            sum += toInt(warningsAggregator.getCheckStyle(job));
        }
        return String.valueOf(sum);
    }

    /**
     * Returns the number of Dry warnings for the specified jobs.
     *
     * @param jobs
     *            the jobs to get the warnings for
     * @return the number of Dry warnings
     */
    public String getDry(final Collection<Job<?, ?>> jobs) {
        int sum = 0;
        for (Job<?, ?> job : jobs) {
            sum += toInt(warningsAggregator.getDry(job));
        }
        return String.valueOf(sum);
    }

    /**
     * Returns the number of FindBugs warnings for the specified jobs.
     *
     * @param jobs
     *            the jobs to get the warnings for
     * @return the number of FindBugs warnings
     */
    public String getFindBugs(final Collection<Job<?, ?>> jobs) {
        int sum = 0;
        for (Job<?, ?> job : jobs) {
            sum += toInt(warningsAggregator.getFindBugs(job));
        }
        return String.valueOf(sum);
    }

    /**
     * Returns the number of PMD warnings for the specified jobs.
     *
     * @param jobs
     *            the jobs to get the warnings for
     * @return the number of PMD warnings
     */
    public String getPmd(final Collection<Job<?, ?>> jobs) {
        int sum = 0;
        for (Job<?, ?> job : jobs) {
            sum += toInt(warningsAggregator.getPmd(job));
        }
        return String.valueOf(sum);
    }

    /**
     * Returns the number of open tasks for the specified jobs.
     *
     * @param jobs
     *            the jobs to get the warnings for
     * @return the number of open tasks warnings
     */
    public String getTasks(final Collection<Job<?, ?>> jobs) {
        int sum = 0;
        for (Job<?, ?> job : jobs) {
            sum += toInt(warningsAggregator.getTasks(job));
        }
        return String.valueOf(sum);
    }

    /**
     * Returns the number of compiler warnings for the specified jobs.
     *
     * @param jobs
     *            the jobs to get the warnings for
     * @return the number of compiler warnings
     */
    @Override
    public String getWarnings(final Collection<Job<?, ?>> jobs) {
        int sum = 0;
        for (Job<?, ?> job : jobs) {
            sum += toInt(warningsAggregator.getCompilerWarnings(job));
        }
        return String.valueOf(sum);
    }

    /**
     * Returns the number of Android lint warnings for the specified jobs.
     *
     * @param jobs
     *            the jobs to get the warnings for
     * @return the number of Android lint warnings
     */
    public String getAndroidLint(final Collection<Job<?, ?>> jobs) {
        int sum = 0;
        for (Job<?, ?> job : jobs) {
            sum += toInt(warningsAggregator.getAndroidLint(job));
        }
        return String.valueOf(sum);
    }

    /**
     * Returns the total number of warnings for all jobs.
     *
     * @param jobs
     *            the jobs to get the warnings for
     * @return the total number of warnings
     */
    public String getTotal(final Collection<Job<?, ?>> jobs) {
        int sum = 0;
        for (Job<?, ?> job : jobs) {
            sum += Integer.parseInt(warningsAggregator.getTotal(job));
        }
        return String.valueOf(sum);

    }

    /**
     * Returns the URL of the referenced project action for the selected job.
     *
     * @param project
     *            the selected project
     * @return the URL of the project action
     */
    public String getUrl(final Job<?, ?> project) {
        return project.getUrl() + new AnalysisProjectAction(project).getUrlName();
    }

    /** Backward compatibility. @deprecated replaced by {@link WarningsAggregator} */
    @Deprecated
    private transient boolean isCheckStyleDeactivated;
    /** Backward compatibility. @deprecated replaced by {@link WarningsAggregator} */
    @Deprecated
    private transient boolean isDryDeactivated;
    /** Backward compatibility. @deprecated replaced by {@link WarningsAggregator} */
    @Deprecated
    private transient boolean isFindBugsDeactivated;
    /** Backward compatibility. @deprecated replaced by {@link WarningsAggregator} */
    @Deprecated
    private transient boolean isPmdDeactivated;
    /** Backward compatibility. @deprecated replaced by {@link WarningsAggregator} */
    @Deprecated
    private transient boolean isOpenTasksDeactivated;
    /** Backward compatibility. @deprecated replaced by {@link WarningsAggregator} */
    @Deprecated
    private transient boolean isWarningsDeactivated;
    @Deprecated
    private transient boolean isAndroidLintDeactivated;
     /**
     * Extension point registration.
     *
     * @author Ulli Hafner
     */
    @Extension(optional = true)
    public static class WarningsPerJobDescriptor extends Descriptor<DashboardPortlet> {
        /**
         * Returns whether the Checkstyle plug-in is installed.
         *
         * @return <code>true</code> if the Checkstyle plug-in is installed,
         *         <code>false</code> if not.
         */
        public boolean isCheckStyleInstalled() {
            return AnalysisDescriptor.isCheckStyleInstalled();
        }

        /**
         * Returns whether the Dry plug-in is installed.
         *
         * @return <code>true</code> if the Dry plug-in is installed,
         *         <code>false</code> if not.
         */
        public boolean isDryInstalled() {
            return AnalysisDescriptor.isDryInstalled();
        }

        /**
         * Returns whether the FindBugs plug-in is installed.
         *
         * @return <code>true</code> if the FindBugs plug-in is installed,
         *         <code>false</code> if not.
         */
        public boolean isFindBugsInstalled() {
            return AnalysisDescriptor.isFindBugsInstalled();
        }

        /**
         * Returns whether the PMD plug-in is installed.
         *
         * @return <code>true</code> if the PMD plug-in is installed,
         *         <code>false</code> if not.
         */
        public boolean isPmdInstalled() {
            return AnalysisDescriptor.isPmdInstalled();
        }

        /**
         * Returns whether the Open Tasks plug-in is installed.
         *
         * @return <code>true</code> if the Open Tasks plug-in is installed,
         *         <code>false</code> if not.
         */
        public boolean isOpenTasksInstalled() {
            return AnalysisDescriptor.isOpenTasksInstalled();
        }

        /**
         * Returns whether the Warnings plug-in is installed.
         *
         * @return <code>true</code> if the Warnings plug-in is installed,
         *         <code>false</code> if not.
         */
        public boolean isWarningsInstalled() {
            return AnalysisDescriptor.isWarningsInstalled();
        }

         /**
          * Returns whether the Android Lint plug-in is installed.
          *
          * @return <code>true</code> if the Android Lint plug-in is installed,
          *         <code>false</code> if not.
          */
        public boolean isAndroidLintInstalled() {
             return AnalysisDescriptor.isAndroidLintInstalled();
        }

         @Override
        public String getDisplayName() {
            return Messages.Portlet_WarningsTable();
        }
    }
}

