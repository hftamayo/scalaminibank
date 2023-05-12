package com.hftamayo.bank.actors

import akka.actor.typed.scaladsl.{ActorContext, Behaviors}
import akka.actor.typed.{ActorRef, Behavior}
import akka.persistence.typed.PersistenceId
import akka.persistence.typed.scaladsl.{Effect, EventSourcedBehavior}
import com.hftamayo.bank.actors.PersistentBankAccount.BankAccountBalanceUpdatedResponse

import java.util.UUID

class Bank {

  //commands = messages
  import PersistentBankAccount.Command._
  import PersistentBankAccount.Response._
  import PersistentBankAccount.Command

  //events
  sealed trait Event
  case class BankAccountCreated(id: String) extends Event

  //state
  case class State(accounts: Map[String, ActorRef[Command]])

  //command handler
  def commandHandler: (context: ActorContext[Command]): (State, Command) => Effect[Event, State] = (state, command) =>
    command match {
      case createCommand @ CreateBankAccount(_, _, _, _) =>
        val id = UUID.randomUUID().toString
        val newBankAccount = context.spawn(PersistentBankAccount(id), id)
        Effect
          .persist(BankAccountCreated(id))
          .thenReply(newBankAccount)(_ => createCommand)
      case updateCmd @ UpdateBalance(id, _, _, replyTo) =>
        state.accounts.get(id) match {
          case Some(account) =>
            Effect.reply(account)(updateCmd)
          case None =>
            Effect.reply(replyTo)(BankAccountBalanceUpdatedResponse(None))
        }
      case GetBankAccount(id, replyTo) =>
        state.accounts.get(id) match {
          case Some(account) =>
        }
    }
  //event handler
  val eventHandler: (State, Event) => State = ???
  //behavior
  def apply(): Behavior[Command] = Behaviors.setup { context =>
    EventSourcedBehavior[Command, Event, State](
      persistenceId = PersistenceId.ofUniqueId("bank"),
      emptyState = State(Map()),
      commandHandler = commandHandler(context),
      eventHandler = eventHandler
    )
  }
}
