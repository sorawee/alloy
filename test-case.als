open util/ordering[State]

sig Vertex {}
sig State {
    S: set Vertex
}

abstract sig Event {
    pre, post: State,
    choosen: one Vertex
}{
    choosen not in pre.S
    post.S = pre.S + choosen
}

fact Traces {
    no first.S
    all s: State - last |  one e: Event | e.pre = s and e.post = s.next
}

check { last.S = Vertex } for 8 but 7 Vertex
