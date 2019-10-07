import akka.actor._

import scala.util.Random

case class InputMsg(list: List[Int])

class Master extends Actor {
  val actors: ActorRef = context.actorOf(Props[Calculator], "summator")

  override def receive: Receive = {
    case msg: InputMsg =>
      actors ! msg
    case avgAnswer: Double =>
      println("avg : " + avgAnswer)
    case _ =>
      println("Unknown msg in Master: " + _)
  }
}

class Calculator extends Actor {
  // функция обработчик сообщений
  override def receive: Receive = {
    case InputMsg(list: List[Int]) =>
      val avg = getAvg(list)
      sender ! avg
    case _ =>
      println("Unknown msg in Calculator: " + _)
  }


  @tailrec private[this] def getAvg(list: List[Int], sum: Int = 0, count: Int = 0): Double = {
    if (list.isEmpty) {
      if (count == 0)
        0
      else sum.toDouble / count
    }
    else getAvg(list.tail, sum + list.head, count + 1)
  }
}

object System extends App {
  val actorSystem = ActorSystem()

  val storageActor: ActorRef = actorSystem.actorOf(Props[Master])

  storageActor ! InputMsg(List.empty)
  storageActor ! InputMsg(List(5))
  storageActor ! InputMsg(List(1,2,3))
  storageActor ! InputMsg(Seq.fill(100)(Random.nextInt).toList)

  //  actorSystem.terminate()
}
