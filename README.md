# basic-tcp-chat

A simple client-server chat application written in Scala.

## How to run it

First, start the server:

    $ sbt server/run

By default, it runs on port `8080`, but it can be changed on the file [server/src/main/scala/Program.scala](https://github.com/gwyddie/basic-tcp-chat/blob/main/server/src/main/scala/Program.scala#L4).

Then, run the clients:

    $ sbt client/run

## Disclaimer

This code looks awful from a Scala FP perspective,
but when I saw it working it felt very satisfying 😌

---

That's all, folks!
