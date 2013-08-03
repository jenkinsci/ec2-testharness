/*
 * The MIT License
 *
 * Copyright (c) 2013, Hugh Perkins
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package org.jenkinsci.plugins.ec2harness;

import static org.mockito.Mockito.when;
import hudson.Extension;
import hudson.model.Descriptor;
import hudson.plugins.ec2.EC2PrivateKey;
import hudson.plugins.ec2.Messages;
import hudson.plugins.ec2.AmazonEC2Cloud;
import hudson.slaves.Cloud;
import hudson.util.FormValidation;
import hudson.util.ListBoxModel;

import java.io.IOException;
import java.net.URL;
import java.util.*;

import javax.servlet.ServletException;

import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;
import org.mockito.Mockito;

import com.amazonaws.AmazonClientException;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.model.KeyPair;

/// This class is the main entry point.  It is a new type 
/// of Cloud.  It also wraps (technically: derives from)
/// the AmazonEC2Cloud class, and drives it.
/// It creates an instance of MockAmazonEC2, which is a mock
/// or 'stubbed' version of the AmazonEC2 interface
public class AmazonEC2CloudTester extends AmazonEC2Cloud
{
	static MockAmazonEC2 mockAmazonEC2;

    @DataBoundConstructor
    public AmazonEC2CloudTester(String accessId, String secretKey, String region, String privateKey, String instanceCapStr, 
    		List<SlaveTemplateForTests> templates) {
        super("mockec2cloud_" + region, accessId, secretKey, privateKey,
         instanceCapStr, templates);
    }
	
	@Override
    public synchronized AmazonEC2 connect() throws AmazonClientException {
		if( connection == null ) {
			mockAmazonEC2 = new MockAmazonEC2();
			connection = mockAmazonEC2;
	    	mockAmazonEC2.setKeyName("testkeypair");
	    	try {
				mockAmazonEC2.setFingerprint(privateKey.getFingerprint());
			} catch (IOException e) {
				e.printStackTrace();
			}
	    	mockAmazonEC2.setPrivateKey(privateKey.getPrivateKey());
		}
		return connection;
	}
	
	@Extension
	public static class DescriptorImpl extends Descriptor<Cloud> {

		@Override
		public String getDisplayName() {
			return "AmazonEC2CloudTester";
		}
		
		public ListBoxModel doFillBidTypeItems( @QueryParameter String accessId,
				@QueryParameter String secretKey, @QueryParameter String region) throws IOException,
				ServletException {
			ListBoxModel model = new ListBoxModel();
			return model;
		}
		
		public ListBoxModel doFillRegionItems(@QueryParameter String accessId,
				@QueryParameter String secretKey, @QueryParameter String region) throws IOException,
				ServletException {
			ListBoxModel model = new ListBoxModel();
			model.add(AmazonEC2Cloud.DEFAULT_EC2_HOST);
			return model;
		}

        public FormValidation doTestConnection( URL ec2endpoint,
                String accessId, String secretKey, String privateKey) throws IOException, ServletException {
            return FormValidation.ok(Messages.EC2Cloud_Success());
        }
	}
}
