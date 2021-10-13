def euler(n):
    result = 1
    factors = {}
    for i in range(2, int(n) + 1):
        while n % i == 0:
            factors[i] = factors.get(i, 0) + 1
            n = n / i
    for i in factors.keys():
        result = result * ((i ** factors[i]) - (i ** (factors[i] - 1)))
    return int(result)
