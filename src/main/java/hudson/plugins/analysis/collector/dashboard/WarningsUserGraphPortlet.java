package hudson.plugins.analysis.collector.dashboard;

import org.kohsuke.stapler.DataBoundConstructor;

import static hudson.plugins.analysis.collector.Messages.*;

import hudson.Extension;
import hudson.model.Descriptor;
import hudson.plugins.analysis.collector.AnalysisProjectAction;
import hudson.plugins.analysis.core.AbstractProjectAction;
import hudson.plugins.analysis.dashboard.AbstractWarningsGraphPortlet;
import hudson.plugins.analysis.graph.AnnotationsByUserGraph;
import hudson.plugins.analysis.graph.BuildResultGraph;
import hudson.plugins.view.dashboard.DashboardPortlet;

/**
 * A portlet that shows the warnings of the last build by user and priority.
 *
 * @author Ulli Hafner
 */
public final class WarningsUserGraphPortlet extends AbstractWarningsGraphPortlet {
    /**
     * Creates a new instance of {@link WarningsUserGraphPortlet}.
     *
     * @param name
     *            the name of the portlet
     * @param width
     *            width of the graph
     * @param height
     *            height of the graph
     * @param dayCountString
     *            number of days to consider
     */
    @DataBoundConstructor
    public WarningsUserGraphPortlet(final String name, final String width, final String height, final String dayCountString) {
        super(name, width, height, dayCountString);

        configureGraph(getGraphType());
    }

    @Override
    protected Class<? extends AbstractProjectAction<?>> getAction() {
        return AnalysisProjectAction.class;
    }

    @Override
    protected String getPluginName() {
        return "analysis";
    }

    @Override
    protected BuildResultGraph getGraphType() {
        return new AnnotationsByUserGraph();
    }

    /**
     * Extension point registration.
     *
     * @author Ulli Hafner
     */
    @Extension(optional = true)
    public static class WarningsGraphDescriptor extends Descriptor<DashboardPortlet> {
        @Override
        public String getDisplayName() {
            return Portlet_WarningsUserGraph();
        }
    }
}

