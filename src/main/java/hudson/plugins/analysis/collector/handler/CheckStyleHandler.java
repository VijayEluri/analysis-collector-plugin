package hudson.plugins.analysis.collector.handler;

import java.util.Collection;

import org.jvnet.localizer.Localizable;

import com.google.common.collect.Lists;

import hudson.plugins.analysis.collector.AnalysisDescriptor;
import hudson.plugins.analysis.core.AbstractProjectAction;
import hudson.plugins.analysis.core.BuildResult;
import hudson.plugins.analysis.core.PluginDescriptor;
import hudson.plugins.analysis.core.ResultAction;
import hudson.plugins.checkstyle.*;

/**
 * Handles access to the Checkstyle plug-in.
 *
 * @author Ulli Hafner
 */
public class CheckStyleHandler implements AnalysisHandler {
    @Override
    public Class<? extends AbstractProjectAction<?>> getProjectActionType() {
        return CheckStyleProjectAction.class;
    }

    @Override
    public String getUrl() {
        return AnalysisDescriptor.CHECKSTYLE;
    }

    @Override
    public Localizable getDetailHeader() {
        return Messages._Checkstyle_Detail_header();
    }

    @Override
    public Class<? extends PluginDescriptor> getDescriptor() {
        return CheckStyleDescriptor.class;
    }

    @Override
    public Collection<? extends Class<? extends ResultAction<? extends BuildResult>>> getResultActions() {
        return Lists.newArrayList(CheckStyleResultAction.class, CheckStyleMavenResultAction.class);
    }
}
