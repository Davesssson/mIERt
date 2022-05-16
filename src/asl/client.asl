// Agent client in project distributed_taxi_system

/* Initial beliefs and rules */

// go_to(X,Y).

/* Initial goals */

!call_for_taxi.

/* Plans */

+setup <-
    !call_for_taxi;
    .abolish(setup).

+!call_for_taxi <-
    ?at(X,Y);
    .abolish(client_called_at(_,_,_));
    .send("headquarters", tell, client_called_at(X,Y)).

+call_again <-
    // this is better than just waiting for three seconds
    .wait(1000);
    .wait(1000);
    .wait(1000);
    .abolish(call_again);
    !call_for_taxi.

+where_to[source(S)] <-
    ?go_to(X,Y);
    .send(S, tell, take_client_to(X,Y));
    .abolish(where_to).
