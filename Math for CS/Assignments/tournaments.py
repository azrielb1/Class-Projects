def length_2_paths(g):
    x = set()
    for i in g[1]:
        for j in g[1]:
            if j[0] == i[1]:
                x.add((i[0], j[1]))
    return x
    
def dominants(g):
    x = set()
    for person in g[0]:
        people_beat = [j[1] for j in g[1] if j[0] == person]
        people_beat.extend([j[1] for j in length_2_paths(g) if j[0] == person])
        if len(set(people_beat)) == len(g[0]) - 1:
            x.add(person)
    return x

t = [("Joe", "Jim", "Jeff", "Jon"),{("Jim", "Jon") ,("Jim", "Jeff"), ("Joe", "Jim"),("Jon", "Joe") ,("Jon", "Jeff"), ("Jeff", "Joe")}]
t = [("a", "b", "c", "d"),{("a", "b") ,("b", "c"), ("a", "d"),("d", "b") ,("c", "a"), ("c", "d")}]
print(length_2_paths(t))
print(dominants(t))