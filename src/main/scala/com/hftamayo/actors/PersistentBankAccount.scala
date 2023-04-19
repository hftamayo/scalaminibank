package com.hftamayo.actors
import akka.actor.typed.ActorRef

class PersistentBankAccount {

  //commands = messages
  sealed trait Command

  case class CreateBankAccount(user: String, currency: String, initialBalance: Double, replyTo: ActorRef[Response])
  case class UpdateBalance(id: String, currency: String, amount: Double /* can be <0 */, replyTo: ActorRef[Response])
  case class GetBankAccount(id: String, replyTo: ActorRef[Response])

  //events = to persist to Cassndra
  trait Event
  case class BankAccountCreated(bankAccount: BankAccount)

  //state
  case class BankAccount(id: String, user: String, currency: String, balance: Double)

  //responses
  sealed trait Response

}
