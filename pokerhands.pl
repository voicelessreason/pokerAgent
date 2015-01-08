%% Ground our terms.
valid_card(F, S) :- valid_face(F), valid_suit(S).
valid_face(2).
valid_face(3).
valid_face(4).
valid_face(5).
valid_face(6).
valid_face(7).
valid_face(8).
valid_face(9).
valid_face(10).
valid_face(jack).
valid_face(queen).
valid_face(king).
valid_face(ace).
valid_suit(clubs).
valid_suit(diamonds).
valid_suit(hearts).
valid_suit(spades).

valid_hand([]).
valid_hand([[F,S]|T]) :- 
	valid_hand(T),
	\+ member([F,S],T).

% count_suits(H,D,C,S)

count_suit(_, [], 0).
count_suit(X,[[F,S]|T], N) :-
	valid_card(F,S),
	X\=S,
	count_suit(X, T, N).
count_suit(X, [[F,S]|T], N) :-
	valid_card(F,S),
	X=S,
	count_suit(X, T, N2),
	N is N2 + 1.

count_suits([],[0,0,0,0]).
count_suits(H,[N1,N2,N3,N4]) :-
	count_suit(hearts,H,N5),
	count_suit(diamonds,H,N6),
	count_suit(clubs,H,N7),
	count_suit(spades,H,N8),
	N1 is N5, N2 is N6,
	N3 is N7, N4 is N8.

% count face & count faces
count_face(_, [], 0).
count_face(X,[[F,S]|T], N) :-
	valid_card(F,S),
	F\=X,
	count_face(X, T, N).
count_face(X, [[F,S]|T], N) :- 
	valid_card(F,S),
	F == X,
	count_face(X, T, N2),
	N is N2 + 1.

count_faces([],[0,0,0,0,0,0,0,0,0,0,0,0,0]).
count_faces([[F,S] | T],[N2,N3,N4,N5,N6,N7,N8,N9,N10,NJ,NQ,NK,NA]) :-
	count_face(2,[[F,S] | T],TWO),
	count_face(3,[[F,S] | T],THREE),
	count_face(4,[[F,S] | T],FOUR),
	count_face(5,[[F,S] | T],FIVE),
	count_face(6,[[F,S] | T],SIX),
	count_face(7,[[F,S] | T],SEVEN),
	count_face(8,[[F,S] | T],EIGHT),
	count_face(9,[[F,S] | T],NINE),
	count_face(10,[[F,S] | T],TEN),
	count_face(jack,[[F,S] | T],JACK),
	count_face(queen,[[F,S] | T],QUEEN),
	count_face(king,[[F,S] | T],KING),
	count_face(ace,[[F,S] | T],ACE),
	N2 is TWO, N3 is THREE, N4 is FOUR, N5 is FIVE, N6 is SIX,
	N7 is SEVEN, N8 is EIGHT, N9 is NINE, N10 is TEN, NJ is JACK,
	NQ is QUEEN, NK is KING, NA is ACE.

% runcount
runCount(_, 0, _).
runCount([H|T], Count, Total) :- H \= 0, Count2 is Count - 1, runCount(T, Count2, Total).
runCount([0|T], Count, Total) :- runCount(T, Total, Total).

% One pair

one_pair(H) :- count_faces(H,L), member(2,L), valid_hand(H).

one_pair(H,O) :- count_faces(H,L), member(2,L), append(H,O,NH), valid_hand(NH).

% two_pair
two_pair(H) :- count_faces(H,L), member(2,L), select(2,L,NL), member(2,NL), valid_hand(H).

two_pair(H, O) :- count_faces(H,L), member(2,L), select(2,L,NL), 
	       member(2,NL), append(H,O,NH), valid_hand(NH).

% three_of_a_kind
three_of_a_kind(H) :- count_faces(H,L), member(3,L), valid_hand(H).

three_of_a_kind(H, O) :- count_faces(H,L), member(3,L), append(H,O,NH), valid_hand(NH).

% four_of_a_kind
four_of_a_kind(H) :- count_faces(H,L), member(4,L), valid_hand(H).

four_of_a_kind(H, O) :- count_faces(H,L), member(4,L), append(H,O,NH), valid_hand(NH).

% flush
flush(H) :- count_suits(H, [_,_,_,X]),
		X > 4, valid_hand(H).
flush(H) :- count_suits(H, [_,_,X,_]),
		X > 4, valid_hand(H).
flush(H) :- count_suits(H, [_,X,_,_]),
		X > 4, valid_hand(H).
flush(H) :- count_suits(H, [X,_,_,_]),
		X > 4, valid_hand(H).

flush(H,O) :- count_suits(H, [_,_,_,X]),
		X > 4, append(H,O,NH), valid_hand(NH).
flush(H,O) :- count_suits(H, [_,_,X,_]),
		X > 4, append(H,O,NH), valid_hand(NH).
flush(H,O) :- count_suits(H, [_,X,_,_]),
		X > 4, append(H,O,NH), valid_hand(NH).
flush(H,O) :- count_suits(H, [X,_,_,_]),
		X > 4, append(H,O,NH), valid_hand(NH).

% straight
straight(H) :- count_faces(H,L), runCount(L, 5, 5), valid_hand(H).

straight(H,O) :- count_faces(H,L), runCount(L, 5, 5), append(H,O,NH), valid_hand(NH).

% full house
full_house(H) :- one_pair(H), three_of_a_kind(H), valid_hand(H).

full_house(H,O) :- one_pair(H), three_of_a_kind(H), append(H,O,NH), valid_hand(NH).

% straight_flush
straight_flush(H) :- straight(H), flush(H), valid_hand(H).

straight_flush(H,O) :- straight(H), flush(H), append(H,O,NH), valid_hand(NH).

royal_flush(H) :- flush(H), count_faces(H,[_,_,_,_,_,_,_,_,N10,NJ,NQ,NK,NA]), 
		N10 > 0, NJ > 0, NQ > 0, NK > 0, NA > 0, valid_hand(H).

royal_flush(H,O) :- flush(H), count_faces(H,[_,_,_,_,_,_,_,_,N10,NJ,NQ,NK,NA]),
		N10 > 0, NJ > 0, NQ > 0, NK > 0, NA > 0, append(H,O,NH), valid_hand(NH).

no_hand(H):- \+ one_pair(H), \+ two_pair(H), \+ three_of_a_kind(H), \+ straight(H), \+ flush(H),
		\+ four_of_a_kind(H), \+ full_house(H), \+ straight_flush(H), \+ royal_flush(H).
