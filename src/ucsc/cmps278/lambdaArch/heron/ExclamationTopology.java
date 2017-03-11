package ucsc.cmps278.lambdaArch.heron;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.twitter.heron.api.Config;
import com.twitter.heron.api.HeronSubmitter;
import com.twitter.heron.api.HeronTopology;
import com.twitter.heron.api.exception.AlreadyAliveException;
import com.twitter.heron.api.exception.InvalidTopologyException;
import com.twitter.heron.api.generated.TopologyAPI;
import com.twitter.heron.api.utils.Utils;

/**
 * Use this class to submit topologies to run on the Heron cluster. You should run your program
 * with the "heron jar" command from the command-line, and then use this class to
 * submit your topologies.
 */
public final class ExclamationTopology {
  private static final Logger LOG = Logger.getLogger(HeronSubmitter.class.getName());

  private  ExclamationTopology() {
  }

  /**
   * Submits a topology to run on the cluster. A topology runs forever or until
   * explicitly killed.
   *
   * @param name the name of the topology.
   * @param heronConfig the topology-specific configuration. See {@link Config}.
   * @param topology the processing to execute.
   * @throws AlreadyAliveException if a topology with this name is already running
   * @throws InvalidTopologyException if an invalid topology was submitted
   */
  public static  void submitTopology(String name, Config heronConfig, HeronTopology topology)
      throws AlreadyAliveException, InvalidTopologyException {
    Map<String, String> heronCmdOptions = Utils.readCommandLineOpts();

    // We would read the topology initial state from arguments from heron-cli
    TopologyAPI.TopologyState initialState;
    if (heronCmdOptions.get("cmdline.topology.initial.state") != null) {
      initialState = TopologyAPI.TopologyState.valueOf(
          heronCmdOptions.get("cmdline.topology.initial.state"));
    } else {
      initialState = TopologyAPI.TopologyState.RUNNING;
    }

    LOG.log(Level.FINE, "To deploy a topology in initial state {0}", initialState);

    TopologyAPI.Topology fTopology =
        topology.setConfig(heronConfig).
            setName(name).
            setState(initialState).
            getTopology();
    assert fTopology.isInitialized();

    if (heronCmdOptions.get("cmdline.topologydefn.tmpdirectory") != null) {
      submitTopologyToFile(fTopology, heronCmdOptions);
    } else {
      throw new RuntimeException("topology definition temp directory not specified");
    }
  }

  // Submits to the file
  private static  void submitTopologyToFile(TopologyAPI.Topology fTopology,
                                           Map<String, String> heronCmdOptions) {
    String dirName = heronCmdOptions.get("cmdline.topologydefn.tmpdirectory");
    if (dirName == null || dirName.isEmpty()) {
      throw new RuntimeException("Improper specification of directory");
    }
    String fileName = dirName + "/" + fTopology.getName() + ".defn";
    BufferedOutputStream bos = null;
    try {
      //create an object of FileOutputStream
      FileOutputStream fos = new FileOutputStream(new File(fileName));
      //create an object of BufferedOutputStream
      bos = new BufferedOutputStream(fos);
      byte[] topEncoding = fTopology.toByteArray();
      bos.write(topEncoding);
      bos.flush();
      bos.close();
    } catch (IOException e) {
      throw new RuntimeException("Error writing topology defn to temp directory " + dirName);
    }
  }

  /**
   * Submits a topology to run on the cluster. A topology runs forever or until
   * explicitly killed.
   */
  // TODO add submit options
  public static  String submitJar(Config config, String localJar) {
    throw new UnsupportedOperationException("submitJar unsupported");
  }
} 