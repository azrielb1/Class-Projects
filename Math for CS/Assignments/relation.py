def is_total(domain, codomain, graph):
    domain_in_graph = [i[0] for i in graph]
    for i in domain:
        if i not in domain_in_graph:
            return False
    return True

def is_function(domain, codomain, graph):
    domain_in_graph = [i[0] for i in graph]
    for i in domain:
        if domain_in_graph.count(i) > 1:
            return False
    return True

def is_injection(domain, codomain, graph):
    codomain_in_graph = [i[1] for i in graph]
    for i in codomain:
        if codomain_in_graph.count(i) > 1:
            return False
    return True

def is_surjection(domain, codomain, graph):
    codomain_in_graph = [i[1] for i in graph]
    for i in codomain:
        if i not in codomain_in_graph:
            return False
    return True
