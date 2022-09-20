#include <iostream>

double FC(double F) {
    return 5. / 9 * (F - 32);
}

int main() {
    const int from = 23, to = 40, by = 1;
    for (int i = from; i <= to; i += by) {
        std::cout << i << " " << FC(i) << "\n";
    }
    return 0;
}