package org.act.tstream.ui.model.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.log4j.Logger;

import backtype.storm.Config;
import backtype.storm.generated.ClusterSummary;
import backtype.storm.utils.NimbusClient;

import org.act.tstream.client.ConfigExtension;
import org.act.tstream.cluster.ClusterState;
import org.act.tstream.common.stats.StatBuckets;
import org.act.tstream.ui.UIUtils;
import org.act.tstream.ui.model.ClusterInfo;
import org.act.tstream.utils.NetWorkUtils;
import org.act.tstream.zk.ZkTool;
import com.google.common.collect.Lists;

/**
 * 
 * @author xin.zhou/Longda
 */
@ManagedBean(name = "clusterpage")
@ViewScoped
public class ClusterPage implements Serializable {

	private static final long serialVersionUID = -6103468603521876731L;

	private static final Logger LOG = Logger.getLogger(ClusterPage.class);

	public static String SINGLE_CLUSTER = "single";
	public static String MULTI_CLUSTER = "multi";
	
	private List<Map> uiClusters = null;
	private List<ClusterInfo> clusterInfos = null;
	private String clusterType;

	public ClusterPage() throws Exception {
		init();
	}

	@SuppressWarnings("rawtypes")
	private void init() throws Exception {

		try {
			LOG.info("ClusterPage init...");
			Map conf = UIUtils.readUiConfig();
			uiClusters = ConfigExtension.getUiClusters(conf);
			
			if (uiClusters != null) {
				clusterType = MULTI_CLUSTER;
			    clusterInfos = new ArrayList<ClusterInfo>();	
			    for (Map cluster : uiClusters) {
				    LOG.debug("Get ui cluster config infor, " + cluster);
				    ClusterInfo clusterInfo = new ClusterInfo();
				    clusterInfo.setClusterName(ConfigExtension.getUiClusterName(cluster));
				    clusterInfo.setZkPort(ConfigExtension.getUiClusterZkPort(cluster));
				    clusterInfo.setZkRoot(ConfigExtension.getUiClusterZkRoot(cluster));
				    clusterInfo.setZkServers(ConfigExtension.getUiClusterZkServers(cluster));
				    clusterInfos.add(clusterInfo);
			    }
			} else {
				clusterType = SINGLE_CLUSTER;
			}

		} catch (Exception e) {
			LOG.error("Failed to get cluster information:", e);
			throw e;
		} finally {
		}
	}

	public List<ClusterInfo> getClusterInfo() {
		return clusterInfos;
	}
	
	public String getClusterType() {
		return clusterType;
	}
	
	public static void main(String[] args) {
		try {
			ClusterPage c = new ClusterPage();
			System.out.println(c.getClusterInfo());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
