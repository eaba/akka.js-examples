package pippo

import akka.actor._

import com.typesafe.config.ConfigFactory

object PingPong {

  /*lazy val conf =
    ConfigFactory
      .parseString("""
      akka {
        loglevel = "DEBUG"
        stdout-loglevel = "DEBUG"
      }""")
      .withFallback(eu.unicredit.Main.defaultConfig)
  */
  lazy val system = ActorSystem("pingpong")//, conf)

  def ppActor(matcher: String, answer: String) = Props(
      new Actor {
        def receive = {
          case matcher =>
            sender ! answer
            println(s"received $matcher sending answer $answer")
        }
      }
    )

  def start = {
/*
    import system.dispatcher
    import scala.concurrent.duration._
    system.scheduler.scheduleOnce(1 second){
      println("ciao")
      system.terminate()
    }
*/
    val ponger = system.actorOf(ppActor("ping", "pong"))
    val pinger = system.actorOf(ppActor("pong", "ping"))

    import system.dispatcher
    import scala.concurrent.duration._
    system.scheduler.scheduleOnce(1 second)(
      pinger.!("pong")(ponger)
    )

    system.scheduler.scheduleOnce(2 seconds){
      pinger ! PoisonPill
      ponger ! PoisonPill
      system.terminate()
    }
  }

}
