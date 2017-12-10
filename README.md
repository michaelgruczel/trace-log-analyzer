# distrubuted transaction monitoring on logfile

Don't take this too serious.
This code is just a simple cli whihc analyzes logs to identify the distributed transactions.
The idea is not far away from the google dapper papers and the open zipkin implementation, but in this case we have just a simple CLI to work on logs.

So i expect a file with log entries of type 

    [start-timestamp] [end-timestamp] [trace] [service-name] [caller-span]->[span]

that could for example be    

    2013-10-23T10:13:01.077Z 2013-10-23T10:13:01.077Z mvcjxvuv service5 uypa56ni->x6pdt6cj
    2013-10-23T10:13:01.074Z 2013-10-23T10:13:01.087Z mvcjxvuv service6 null->uypa56ni
    2013-10-23T10:13:01.082Z 2013-10-23T10:13:01.087Z mvcjxvuv service7 uypa56ni->ilsk65z5
    2013-10-23T10:13:03.503Z 2013-10-23T10:13:03.520Z xhxufjkj service5 ilsk65z5->775vliwv
    
and i expect a json representation for each entry as result in the format

    {"id":"mvcjxvuv","root":{"start":"2013-10-23T10:13:01.074Z","end":"2013-10-23T10:13:01.087Z","service":"service6","span":"uypa56ni","calls":[{"start":"2013-10-23T10:13:01.075Z","end":"2013-10-23T10:13:01.076Z","service":"service8","span":"4cgno7ks","calls":[{"start":"2013-10-23T10:13:01.075Z","end":"2013-10-23T10:13:01.075Z","service":"service5","span":"5m4ank7l","calls":[]},{"start":"2013-10-23T10:13:01.076Z","end":"2013-10-23T10:13:01.076Z","service":"service4","span":"lmcitlo2","calls":[]}]},{"start":"2013-10-23T10:13:01.077Z","end":"2013-10-23T10:13:01.077Z","service":"service5","span":"x6pdt6cj","calls":[]},{"start":"2013-10-23T10:13:01.082Z","end":"2013-10-23T10:13:01.087Z","service":"service7","span":"ilsk65z5","calls":[]}]}}
