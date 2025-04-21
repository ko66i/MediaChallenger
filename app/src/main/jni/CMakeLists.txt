cmake_minimum_required(VERSION 3.31.6)

add_library(
        equalizer

        SHARED

        equalizer.cpp)

target_link_libraries(
        equalizer
        android
        log )
