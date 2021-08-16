# Description
Classify is a tool to automate classical music file organization and metadata.

# How it works
Classify is a command line tool. Use `classify help` to see exact usage.

The basic file structure classify uses is as follows:
```
.
└── Composer
    └── Composition
        └── Recording
            └── Audio Files
```
This allows files to have correct metadata (e.g. the composer is NOT listed as the artist) while preserving the natural hierarchy for thinking about composed music (Composer -> Composition -> Recording). Furthermore, this system removes nearly all ambiguity including compositions with the same name, different recordings of the same composition and even different records of the same composition by the same orchestra and conductor.

Classify infers which composer, composition, or recording is being referred to by working directory.

# An example
The following demonstrates the process of adding a recording (Beethoven 5) from scratch:
```
$ mkdir music
$ cd music
$ classify init
$ classify add C "Ludwig van Beethoven"
$ cd "Ludwig van Beethoven"
$ classify add c -f Symphony -n 5 -O 67 -k "C Minor" -m "Allegro con brio; Andante con moto; Scherzo. Allegro - Trio; Allegro"
$ cd "Ludwig van Beethoven: Symphony No. 5 in C Minor, Op. 67"
$ classify add r -y 2000 -c "Simon Schindler" -o "Fulda Symphonic Orchestra" -f ../../../beethoven-5-files/*
$ cd ../..
$ tree
.
└── Ludwig van Beethoven
    └── Ludwig van Beethoven: Symphony No. 5 in C Minor, Op. 67
        └── Fulda Symphonic Orchestra, Simon Schindler (2000)
            ├── Ludwig van Beethoven: Symphony No. 5 in C Minor, Op. 67: III. Scherzo. Allegro - Trio.mp3
            ├── Ludwig van Beethoven: Symphony No. 5 in C Minor, Op. 67: II. Andante con moto.mp3
            ├── Ludwig van Beethoven: Symphony No. 5 in C Minor, Op. 67: I. Allegro con brio.mp3
            └── Ludwig van Beethoven: Symphony No. 5 in C Minor, Op. 67: IV. Allegro.mp3
```
