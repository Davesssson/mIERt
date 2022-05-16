// Agent taxi in project distributed_taxi_system

/* Initial beliefs and rules */

not_busy.

/* Initial goals */

/* Plans */

+!at(X,Y) : at(X,Y) <- true.

+!at(X,Y) : at(X0,Y0) <-
    if(X0 < X) {
        move(right);
    }
    elif(X0 > X) {
        move(left);
    }
    elif(Y0 < Y) {
        move(down);
    }
    elif(Y0 > Y) {
        move(up);
    };
    .abolish(at(X0,Y0));
    .perceive;
    !at(X,Y).

-!at(X,Y) <- !at(X,Y).

+client_called_at(C,X,Y)[source(S)] : not_busy <-
    ?at(X0,Y0);
    .send(S,tell,client_cost(math.abs(X-X0)+math.abs(Y-Y0),C));
    .abolish(client_called_at(C,X,Y)).

+client_called_at(C,X,Y)[source(S)] : busy <-
    .send(S,tell,client_cost(10000,C));
    .abolish(client_called_at(C,X,Y)).

+client_waiting_at(C,X,Y) : not_busy <-
    +busy;
    .abolish(not_busy);
    ?at(X0,Y0);
    if(Y0 < Y) {
        !at(X,Y-1);
    }elif(Y0 > Y) {
        !at(X,Y+1);
    }
    .abolish(client_waiting_at(C,X,Y));
    .send("headquarters", tell, client_picked_up(C)).

+client_waiting_at(C,X,Y)[source(S)] : busy <-
    .send(S,tell,reject(C,X,Y));
    .abolish(client_waiting_at(C,X,Y)).

+pickup_ack(C) <-
    .abolish(pickup_ack(C));
    .send(C,tell,where_to).

+take_client_to(X,Y)[source(C)] <-
    remove(C);
    !at(X,Y);
    +not_busy;
    .abolish(busy);
    .abolish(take_client_to(X,Y)).