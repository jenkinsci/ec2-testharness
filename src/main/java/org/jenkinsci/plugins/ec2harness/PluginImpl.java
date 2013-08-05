package org.jenkinsci.plugins.ec2harness;

import java.io.IOException;

import javax.servlet.ServletException;

import net.sf.json.JSONObject;

import org.kohsuke.stapler.StaplerRequest;

import hudson.*;
import hudson.model.*;
import hudson.model.Descriptor.FormException;
import hudson.slaves.NodeProvisioner;
import hudson.slaves.NodeProvisioner.NodeProvisionerInvoker;
import hudson.tasks.Builder;

public class PluginImpl extends Plugin implements Describable<PluginImpl> {
	@Override
	public void postInitialize() throws Exception {
		getDescriptor().load();
		if( getDescriptor().activateFastScheduler ) {
			System.out.println( this.getClass().getCanonicalName() + " Changing Clock and NodeProvisioner delays, to improve testing speed");
			LoadStatistics.CLOCK = 4 * 1000;
			NodeProvisionerInvoker.INITIALDELAY = LoadStatistics.CLOCK * 1;
			NodeProvisionerInvoker.RECURRENCEPERIOD = LoadStatistics.CLOCK * 1;
		} else {
			System.out.println( this.getClass().getCanonicalName() + " NOT changing Clock and NodeProvisioner delays.  Tests will execute slowly (minutes...).  Please activate 'activateFastScheduler' option in the 'configure' page to accelerate testing" );			
		}
	}

	public DescriptorImpl getDescriptor() {
        return (DescriptorImpl)Hudson.getInstance().getDescriptorOrDie(getClass());
    }

    public static PluginImpl get() {
        return Hudson.getInstance().getPlugin(PluginImpl.class);
    }

    @Extension
    public static final class DescriptorImpl extends Descriptor<PluginImpl> {
        @Override
        public String getDisplayName() {
            return "EC2 test harness PluginImpl";
        }
		private boolean activateFastScheduler = false;
		
		@Override
		public boolean configure(StaplerRequest req, JSONObject json)
				throws FormException {
			req.bindJSON(this, json);
			save();
			return super.configure(req, json);
		}
		
		public void setActivateFastScheduler(boolean activateFastScheduler ) {
			this.activateFastScheduler = activateFastScheduler;
		}

		public boolean getActivateFastScheduler() {
			return activateFastScheduler;
		}
    }
}
