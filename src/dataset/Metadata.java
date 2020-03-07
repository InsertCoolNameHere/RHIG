/*
Copyright (c) 2018, Computer Science Department, Colorado State University
All rights reserved.

Redistribution and use in source and binary forms, with or without modification,
are permitted provided that the following conditions are met:

1. Redistributions of source code must retain the above copyright notice, this
   list of conditions and the following disclaimer.
2. Redistributions in binary form must reproduce the above copyright notice,
   this list of conditions and the following disclaimer in the documentation
   and/or other materials provided with the distribution.

This software is provided by the copyright holders and contributors "as is" and
any express or implied warranties, including, but not limited to, the implied
warranties of merchantability and fitness for a particular purpose are
disclaimed. In no event shall the copyright holder or contributors be liable for
any direct, indirect, incidental, special, exemplary, or consequential damages
(including, but not limited to, procurement of substitute goods or services;
loss of use, data, or profits; or business interruption) however caused and on
any theory of liability, whether in contract, strict liability, or tort
(including negligence or otherwise) arising in any way out of the use of this
software, even if advised of the possibility of such damage.
*/
package dataset;

import java.io.IOException;

import rigFeature.Feature;
import rigFeature.FeatureArray;
import rigFeature.FeatureArraySet;
import rigFeature.FeatureSet;

public class Metadata {

	private String name = "";

	/**
	 * Metadata attributes: these Features are represented by a 1D array and are
	 * accessed as a simple key-value store.
	 */
	private FeatureSet attributes = new FeatureSet();

	/**
	 * A key-value store for multidimensional {@link FeatureArray}s.
	 */
	private FeatureArraySet features = new FeatureArraySet();

	/**
	 * The duration by which to hash bins. It is assumed that this is in SECONDS*/
	private double duration; 
	
	/**
	 * Creates an unnamed Metadata instance
	 */
	public Metadata() {
	}

	/**
	 * Creates a named Metadata instance.
	 */
	public Metadata(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		if (name == null) {
			this.name = "";
		} else {
			this.name = name;
		}
	}
	

	/**
	 * Places a single feature into this Metadata instance's attribute
	 * FeatureSet.
	 */
	public void putAttribute(Feature feature) {
		attributes.put(feature);
	}

	public Feature getAttribute(String featureName) {
		return attributes.get(featureName);
	}

	public FeatureSet getAttributes() {
		return attributes;
	}
	
	/**
	 * Sets this Metadata container's attribute FeatureSet. This will eliminate
	 * any previously-added attributes.
	 *
	 * @param attributes
	 *            {@link FeatureSet} containing attributes that should be
	 *            associated with this Metadata instance.
	 */
	public void setAttributes(FeatureSet attributes) {
		this.attributes = attributes;
	}
	

	public void putFeature(FeatureArray feature) {
		features.put(feature);
	}
	
	public void setDuration(double dur)throws IllegalArgumentException{
		if (dur <= 0)
			throw new IllegalArgumentException("Metadata can't have a duration <= 0");
		this.duration = dur;
	}
	
	public double getDuration(){
		return this.duration;
	}
	public FeatureArray getFeature(String featureName) {
		return features.get(featureName);
	}

	/**
	 * Sets this Metadata container's set of Feature arrays. This will eliminate
	 * any previously-added Feature arrays.
	 *
	 * @param features
	 *            {@link FeatureArraySet} containing features that should be
	 *            associated with this Metadata instance.
	 */
	public void setFeatures(FeatureArraySet features) {
		this.features = features;
	}

	public FeatureArraySet getFeatures() {
		return features;
	}

	
	
	@Override
	public String toString() {
		String nl = System.lineSeparator();
		String str = "Name: '" + name + "'" + nl ;
		

		str += "Number of Attributes: " + attributes.size() + nl;
		for (Feature f : attributes) {
			str += f.toString() + nl;
		}

		str += "Number of ND Feature Arrays: " + features.size() + nl;

		return str;
	}

	
}
