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

import java.util.ArrayList;
import java.util.Random;

import com.amazonaws.*;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.model.*;

/// Add some overrides so we can simulate certain actions, 
/// like creating instances and iterating over keypairs
public class MockAmazonEC2 extends AmazonEC2Adapter {
	private ArrayList<Instance> instances = new ArrayList<Instance>();
	private String keyName;
	private String privateKey;
	private String fingerprint;
	
	void printcall() {
		StackTraceElement me = Thread.currentThread().getStackTrace()[2];
//		System.out.println(me.getClassName() + "." + me.getMethodName() + " line " + me.getLineNumber() );
	}
	
	public void setKeyName(String keyName ){
		this.keyName = keyName;
	}
	
	public void setPrivateKey( String privateKey ) {
		this.privateKey = privateKey;
	}
	
	public String getKeyname() {
		return keyName;
	}
	
	public String getPrivateKey() {
		return privateKey;
	}
	
	public void setFingerprint( String fingerprint ) {
		this.fingerprint = fingerprint;
	}
	
	public String getFingerprint() {
		return fingerprint;
	}

	@Override
	public RunInstancesResult runInstances(
			RunInstancesRequest runInstancesRequest)
			throws AmazonServiceException, AmazonClientException {
		printcall();

		Instance instance = new Instance();
		Random random = new Random();
		instance.setInstanceId("" + random.nextInt(10000));
		InstanceState instanceState = new InstanceState();
		instanceState.setName(InstanceStateName.Running);
		instance.setState(instanceState);

		ArrayList<Instance> instances = new ArrayList<Instance>();
		instances.add(instance);
		this.instances.add(instance);

		Reservation reservation = new Reservation();
		reservation.setInstances(instances);

		RunInstancesResult runInstancesResult = new RunInstancesResult();
		runInstancesResult.setReservation(reservation);

		return runInstancesResult;
	}


	@Override
	public DescribeInstancesResult describeInstances(
			DescribeInstancesRequest describeInstancesRequest)
			throws AmazonServiceException, AmazonClientException {
		printcall();
		DescribeInstancesResult describeInstancesResult = new DescribeInstancesResult();
		if( instances.size() == 0 ) {
			return describeInstancesResult;
		}
		Reservation reservation = new Reservation();
		ArrayList<Reservation> reservations = new ArrayList<Reservation>();
		reservations.add( reservation );
		describeInstancesResult.setReservations(reservations);
		reservation.setInstances(instances);

		return describeInstancesResult;
	}

	@Override
	public DescribeInstancesResult describeInstances()
			throws AmazonServiceException, AmazonClientException {
		System.out.println("MockAmazonEC2.describeinstances() num instances: " + this.instances.size() );
		printcall();
		DescribeInstancesResult describeInstancesResult = new DescribeInstancesResult();
		ArrayList<Reservation> reservations = new ArrayList<Reservation>();
		describeInstancesResult.setReservations(reservations);
		Reservation reservation = new Reservation();
		reservation.setInstances(instances);
		reservations.add(reservation);
		return describeInstancesResult;
	}

	public DescribeKeyPairsResult describeKeyPairs()
			throws AmazonServiceException, AmazonClientException {
		printcall();
		DescribeKeyPairsResult describeKeyPairsResult = new DescribeKeyPairsResult();
		ArrayList<KeyPairInfo> keyPairs = new ArrayList<KeyPairInfo>();
		KeyPairInfo myKey = new KeyPairInfo();
		myKey.setKeyFingerprint(fingerprint);
		myKey.setKeyName(keyName);
		keyPairs.add(myKey);
		describeKeyPairsResult.setKeyPairs(keyPairs);
		return describeKeyPairsResult;
	}
}
