## Network Configuration Parser
This is an example of a Network Configuration Parser written in Clojure and using the Instaparse library

### Dependencies
Install Clojure and command line tools ( see [Getting Started](https://clojure.org/guides/getting_started) )

## Demo
https://asciinema.org/a/VIc56RLOrs17qEzwJC2Y3U7Ce


#### Notes:

Pull raw content from github
https://raw.githubusercontent.com/gaberger/ncp_parser/master/configs/frr/router1.cfg


## FRR commands

```
sudo snap run frr.vtysh -h
Usage : vtysh [OPTION...]

Integrated shell for FRR.

-b, --boot               Execute boot startup configuration
-c, --command            Execute argument as command
-d, --daemon             Connect only to the specified daemon
-f, --inputfile          Execute commands from specific file and exit
-E, --echo               Echo prompt and command in -c mode
-C, --dryrun             Check configuration for validity and exit
-m, --markfile           Mark input file with context end
    --vty_socket         Override vty socket path
    --config_dir         Override config directory path
-w, --writeconfig        Write integrated config (frr.conf) and exit
-h, --help               Display this help and exit
```

Note that multiple commands may be executed from the command
line by passing multiple -c args, or by embedding linefeed
characters in one or more of the commands.

Report bugs to https://github.com/frrouting/frr/issues

```sudo snap run frr.vtysh -c "show run"```
