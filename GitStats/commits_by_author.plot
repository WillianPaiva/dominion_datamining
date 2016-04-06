set terminal png transparent size 640,240
set size 1.0,1.0

set terminal png transparent size 640,480
set output 'commits_by_author.png'
set key left top
set yrange [0:]
set xdata time
set timefmt "%s"
set format x "%Y-%m-%d"
set grid y
set ylabel "Commits"
set xtics rotate
set bmargin 6
plot 'commits_by_author.dat' using 1:2 title "WillianPaiva" w lines, 'commits_by_author.dat' using 1:3 title "Elmer Bayol" w lines, 'commits_by_author.dat' using 1:4 title "Martha Lopez Farfan" w lines, 'commits_by_author.dat' using 1:5 title "Khaoula Tagnaouti" w lines, 'commits_by_author.dat' using 1:6 title "willian ver valen" w lines, 'commits_by_author.dat' using 1:7 title "ebayol" w lines, 'commits_by_author.dat' using 1:8 title "wvervale@U-BORDEAUX.FR" w lines, 'commits_by_author.dat' using 1:9 title "root" w lines
