# Snow Family implementation and possible applications
This repository shows a possible implementation of the Snow Family protocols in the Alchemist environment.
The Slush, Snowflake, Snowball and Blizzard can be run through the command 
```bash 
.\gradlew runProtocolNameGraphic
```
as shown in the Alchemist and Collektive documentation.

The implementation does not reach a 1:1 behaviour as expected from the original paper's claims, and exposes some failures while trying to do so. In particular, consensus is not reached in the network when facing bottlenecks between two subnetworks with different opinions.
While this could be caused by an error on the programming side, and not because of the asynchronous nature of Alchemist, it does raise an interesting question: how are bottlenecks defined and how can they be found in a network?

## Finding bottlenecks in a network
It seems like nodes that work as "walls" for the propagation of a certain opinion in the network have a relatively low rank compared to the other nodes in the network, and tend to change opinion at a much higher frequency than the rest of the network. They are placed between bigger clusters that do not have many other nodes connected to each other. This leads to think that contended nodes (that is, nodes that change opinions often) could be classified as bottlenecks. 
This, though, does not imply that all bottlenecks are contended, simply because one bottleneck could have more connections to one side of the network than another; also, it is necessary to define at which point a node can be considered "contended". Since it seems like frequency of opinion change is directly related to the rank of the node, one could define a threshold of number of changes of opinion after which the node is considered contended that is directly related to the rank of the node. But would this still apply in a network where nodes do not have a static position? If nodes could move around freely, when should the counter be reset because of the change of topology, and when should it be kept instead?

## boozeControl
Trying to answer to these questions, the boozeControl package implements methods to take advantage of the Snow Family protocols to locate bottlenecks in the network. As of now, the only method implemented is "contendedFinder", and the respective .yaml is called contended.yaml.
In this algorithm, as mentioned above, a node is considered contended if it changes opinion n times, where n > UserDefinedThreshold. The threshold in the code is defined depending on the number of nodes in the network: this does not correctly represent the model since frequency of opinion change is mostly related to the node rank; the speed at which the network reaches consensus, though, should be directly related to the logarithm of the number of nodes in the network: therefore a subnetwork of M nodes would almost certainly have reached consensus in at most logM rounds. If a node in less than logM nodes didn't reach consensus, it is probably in a situation of uncertainty. Given that M changes with the spread of opinion, the choice is to use N as an indicator for the threshold instead.
Plotting a mean of the how the frequency changes depending on the size of the network could be useful to find an exact relationship between the two of them.

## Acknowledgements

This project builds upon:

- [Collektive](https://github.com/Collektive/collektive)
- [Collektive Experiments Bootstrap](https://github.com/Collektive/collektive-experiments-bootstrap)
- [Alchemist](https://github.com/AlchemistSimulator/Alchemist)

The original projects retain their respective authors
and licenses.