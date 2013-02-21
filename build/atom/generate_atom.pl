#!/usr/bin/env perl
use strict;
use warnings;
use Data::Dumper;


use constant PI => 4 * atan2(1, 1);

my $width = 960;
my $height = 960;
my $center_x = $width / 2;
my $center_y = $height / 2;
my $radius_x = 400;
my $radius_y = 120;
my $netron_radius = 60;
my $nb_images = 20;

my $icon_size = 128;

#
# Return coordinate of an ellipse dot
#
#x = u + a cos (t);
#y = v + b sin (t);
sub ellipse_dot
{
    my ($rad_angle) = @_;

    return ($center_x + $radius_x * cos ($rad_angle),
            $center_y + $radius_y * sin ($rad_angle));

}


#
# draw an ellipse (debug)
# 
sub ellipse_dotted
{
    print "  <path d=\"";
    my $NB_STEP = 30;
    for (my $i = 0; $i <= $NB_STEP; $i++) {
        my $rad_angle = $i * 2 * PI / $NB_STEP;
        
        if ($i == 0) {
            print "M ";
        } else {
            print "L ";
        }
        printf("%.3f,%.3f ", ellipse_dot($rad_angle));
    }
    printf "\" transform=\"rotate(30, %d, %d)\"/>\n", $center_x, $center_y;
}



#
# Draw one netron
#
# $fd                          : file descriptor to write result
# $netron_rad_angle            : where is the netron on the ellipse
# $ellipse_rotation_deg_angle  : rotation of the ellipse itself (in deg, i know)
# $netron_fill                 : reference to filling defined in <def> section of the svg
#
sub draw_netron
{
    my ($fd, $netron_rad_angle, $ellipse_rotation_deg_angle, $netron_fill) = @_;

    my @ellipse_dot = ellipse_dot($netron_rad_angle);

    print $fd "  ";

    if ($ellipse_rotation_deg_angle) {
        # Rotate the ellipse
        printf $fd "<g transform=\"rotate(%d, %d, %d) \">", $ellipse_rotation_deg_angle, $center_x, $center_y;
        # Rotate the sphere to preserve gradiant orientation
        printf $fd "<g transform=\"rotate(%d, %d, %d) \">", -$ellipse_rotation_deg_angle, @ellipse_dot;
    }


    my $radius = $netron_radius * (1 - 0.50 * (sin(-$netron_rad_angle) + 1) / 2);
    printf($fd "<circle r=\"%.4f\" style=\"fill:url(#%s);\" transform=\"translate(%.4f, %.4f)\" />", $radius, $netron_fill, @ellipse_dot); 

    if ($ellipse_rotation_deg_angle) {
        print $fd "</g></g>";
    }
    
    print $fd "\n";
}


#
# Draw one ellipse
#
# $fd                          : file descriptor to write result
# $ellipse_rotation_deg_angle  : rotation of the ellipse itself (in deg, i know)
#
sub draw_ellipse
{
    my ($fd, $ellipse_rotation_deg_angle) = @_;

    print $fd "  ";


    if ($ellipse_rotation_deg_angle) {
        printf $fd "<g transform=\"rotate(%d, %d, %d) \">", $ellipse_rotation_deg_angle, $center_x, $center_y;
    }


    my @left_dot = ($center_x - $radius_x,  $center_y);
    my @right_dot = ($center_x + $radius_x,  $center_y);
    my $zoom = 0.98;
    my $inner_radius_x = $zoom * $radius_x;
    my $inner_radius_y = $zoom * $radius_y;
    my $dec = $inner_radius_y - $radius_y + 1;
    my @inner_left_dot = ($center_x - $inner_radius_x,  $center_y + $dec);
    my @inner_right_dot = ($center_x + $inner_radius_x,  $center_y + $dec);
    

    
    printf $fd "<path  style=\"fill:#000000;fill-opacity:0.4;fill-rule:evenodd;stroke:none\"
               d=\"M %f %f 
                  A %f %f 0 0 0 %f,%f 
                  A %f %f 0 0 0 %f,%f z
                  M %f %f
                  A %f %f 0 0 0 %f,%f
                  A %f %f 0 0 0 %f,%f z
        \"/>",

                  # base ellipse
                  @left_dot,
                  
                  $radius_x, $radius_y,
                  @right_dot,
                  
                  $radius_x, $radius_y,
                  @left_dot,
                  
                  
                  # inner ellipse
                  @inner_left_dot,
                  
                  $inner_radius_x, $inner_radius_y,
                  @inner_right_dot,
                  
                  $inner_radius_x, $inner_radius_y,
                  @inner_left_dot,
    ;


    if ($ellipse_rotation_deg_angle) {
        print $fd "</g>";
    }
    
    print $fd "\n";
}



#
# Generate an SVG for the given step
#
sub draw_svg
{
    my ($step) = @_;

    my $finename = sprintf('atom_%03d.svg', $step);

    open(my $fd, "> $finename") or die ("Cannot open SVG file '$finename'!");

    printf($fd "<!DOCTYPE svg PUBLIC \"-//W3C//DTD SVG 20010904//EN\" \"http://www.w3.org/TR/2001/REC-SVG-20010904/DTD/svg10.dtd\">

<svg width=\"%d\" height=\"%d\" xml:lang=\"fr\"
     xmlns=\"http://www.w3.org/2000/svg\"
     xmlns:xlink=\"http://www.w3.org/1999/xlink\">

  <defs>
    <radialGradient id=\"kernel\" cx=\"30%%\" cy=\"30%%\" r=\"80%%\" fx=\"30%%\" fy=\"30%%\">
      <stop offset=\"0%%\" style=\"stop-color:rgb(255,255,255);stop-opacity:1\" />
      <stop offset=\"100%%\" style=\"stop-color:rgb(0, 0, 0);stop-opacity:1\" />
    </radialGradient>

    <radialGradient id=\"grad1\" cx=\"30%%\" cy=\"30%%\" r=\"80%%\" fx=\"30%%\" fy=\"30%%\">
      <stop offset=\"0%%\" style=\"stop-color:rgb(255,255,255);stop-opacity:1\" />
      <stop offset=\"100%%\" style=\"stop-color:rgb(0,0,200);stop-opacity:1\" />
    </radialGradient>
    <radialGradient id=\"grad2\" cx=\"30%%\" cy=\"30%%\" r=\"80%%\" fx=\"30%%\" fy=\"30%%\">
      <stop offset=\"0%%\" style=\"stop-color:rgb(255,255,255);stop-opacity:1\" />
      <stop offset=\"100%%\" style=\"stop-color:rgb(0,150,0);stop-opacity:1\" />
    </radialGradient>
    <radialGradient id=\"grad3\" cx=\"30%%\" cy=\"30%%\" r=\"80%%\" fx=\"30%%\" fy=\"30%%\">
      <stop offset=\"0%%\" style=\"stop-color:rgb(255,255,255);stop-opacity:1\" />
      <stop offset=\"100%%\" style=\"stop-color:rgb(255,0,0);stop-opacity:1\" />
    </radialGradient>
  </defs>

", $width, $height);


    printf($fd "<circle r=\"%d\" style=\"fill:url(#kernel);\" transform=\"translate(%d, %d)\"/>",
           $netron_radius * 1.1,
           $center_x, $center_y);


    # Arbitrary (looks cool...)
    my $ellipse_angle_1 = 195;
    my $ellipse_angle_2 = 75;
    my $ellipse_angle_3 = 320;
    my $start_angle_1 = 3.4;
    my $start_angle_2 = 1.4;
    my $start_angle_3 = 2.8;

    my $netron_angle;


    draw_ellipse($fd, $ellipse_angle_1);
    draw_ellipse($fd, $ellipse_angle_2);
    draw_ellipse($fd, $ellipse_angle_3);


    $netron_angle = $step * 2 * PI / $nb_images + $start_angle_1;
    draw_netron($fd, $netron_angle, $ellipse_angle_1, 'grad1');

    $netron_angle = $step * 2 * PI / $nb_images + $start_angle_2;
    draw_netron($fd, $netron_angle, $ellipse_angle_2, 'grad2');

    $netron_angle = $step * 2 * PI / $nb_images + $start_angle_3;
    draw_netron($fd, $netron_angle, $ellipse_angle_3, 'grad3');


    print $fd "</svg>\n";
    close($fd);
}





#
# Main
#

for (my $i = 0; $i < $nb_images; $i++) {
    my $svg = sprintf('atom_%03d.svg', $i);
    my $png = sprintf('atom_%03d.png', $i);
    draw_svg($i);
    system("convert -background none -resize ${icon_size}x${icon_size} $svg $png");
}



#draw_svg(0);






