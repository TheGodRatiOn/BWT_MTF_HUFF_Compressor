# BWT_MTF_HUFF_Compressor
Text data compressor based on combination of BWT and MTF algorithms with Huffman encoding.

java version - 11

command line interface parameters: 
<input filename/filepath> <output filename/filepath>

Out of the box size of text buffer - 250 KB (can be changed in source code)

It is recommended to change input encoder buffer size for your needs,
because efficiency of compression is increasing alongside it,
but it comes with a cost of performance.

Compression efficiency over H(X) is guaranteed for text data.
Average efficiency is around H(X|X), in some specific cases may reach H(X|X) < BPS < H(X|XX),
where "BPS" is number of bites per symbol of ASCII alphabet.