/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package temaDeLaSemana;

import java.util.SortedMap;

/**
 *
 * @author Rodrigo
 */
public class NoticiaCluster {
	private String url;
	private SortedMap<String, Double> vectNoticia;
	private Integer numCluster;
	
	NoticiaCluster(String url, SortedMap<String, Double> vectNoticia){
		this.url = url;
		this.vectNoticia = vectNoticia;
		this.numCluster = -1;
	}
	
	@Override
	public String toString(){
		return url + " : " + numCluster;
	}

	/**
	 * @return the url
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * @return the vectNoticia
	 */
	public SortedMap<String, Double> getVectNoticia() {
		return vectNoticia;
	}

	/**
	 * @return the numCluster
	 */
	public Integer getNumCluster() {
		return numCluster;
	}

	/**
	 * @param numCluster the numCluster to set
	 */
	public void setNumCluster(Integer numCluster) {
		this.numCluster = numCluster;
	}
}
