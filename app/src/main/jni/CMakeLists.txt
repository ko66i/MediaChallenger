# Especifica a versão mínima do CMake necessária para construir este projeto
cmake_minimum_required(VERSION 3.31.6)

# Cria uma biblioteca compartilhada chamada "equalizer" a partir do arquivo fonte equalizer.cpp
add_library(
        equalizer     # Nome da biblioteca
        SHARED        # Tipo da biblioteca: compartilhada (dinâmica)
        equalizer.cpp # Arquivo(s) fonte para a biblioteca
)

# Faz o link da biblioteca "equalizer" com as bibliotecas Android e log
target_link_libraries(
        equalizer     # Biblioteca alvo para adicionar as dependências
        android       # Biblioteca nativa do Android
        log           # Biblioteca de logging do Android
)
