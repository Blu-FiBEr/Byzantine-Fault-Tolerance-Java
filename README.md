# Byzantine Fault Tolerance (Java)
- A java implementation of Byzantine Broadcast in a sample problem of faulty machines trying to stay on the same path.
- The problem is as follows:
  - There are N machines trying to stay on the same path.
  - At most t of these machines are faulty(malicious), where t < n/3
  - The honest machines have to remain on the same path.
  - In every instance of the algorithm, one of the machines is chosen as the leader (sender)
  - The leader sends a binary message which tells whether to go right or left
  - As long as broadcast has validity, consistency and liveliness, the honest machines remain on the same path.

- To make the problem practically simple, following assumptions are taken:
  - The leader either sends a message to everyone or to no one.
  - The leader sends at least (2t + 1) identical messages.
  - The messages are also sent in a bounded time to manifest a synchronous setting.
  

 

