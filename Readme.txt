    cs251: CoPaDS Friendly Integer Assignment Status, Analysis and Feedback

    My name is:      David Pitoniak

    Assignment is:   Project 2: Friendly Integers Analysis.


    ============= Status ==============================
    Remove choices inside the [] below to leave one answer on the next line:
        works
    My program [works] [crashes] [hangs] [is incomplete] [other].

    Explain your answer with an explanation as appropriate.
        Both FriendlySeq and FriendlySmp create output as shown on Project writeup.

    ============= Analysis ============================

    1. How much time did you spend on this project?
    Try if you can to identify setup time, (re)design time, debug time,
    and test time as time subsegments of your answer.

        approximately 25 hours.

    2. Identify the host you used for the runs below and
    how many cores the system had; lscpu -e -b will answer that for you.
    You will have to use this in your analysis for the other questions.

        stark - 12 cores

    3. Using the debug=makespan tracker=none command line options of pj2,
    enter results of the following runs of your sequential and parallel programs.

    If you were unable to get your Friendly programs working, then use
    Kaminsky's PiSeq and PiSmp example programs, and
    apply those programs to the value 10,000,000,000 (1 and 10 0s).

    For the FriendlySeq and FriendlySmp programs, run these using
    a span of about 1 to 2 million numbers; for example, 1000 1000000 or
    50 2000000 should be a big enough range to gather performance data
    for speedup and efficiency analysis. Of course, the range must be
    the same for both programs.

    a. java pj2 debug=makespan FriendlySeq 1000 1000000

        7726 msec

    b. java pj2 debug=makespan FriendlySmp 1000 1000000

        3567 msec

    4. speedup and efficiency

    a. calculate the speedup of #2 above;
    see lecture notes from week 05 or [BCBD] for how to calculate it.

        2.16 times faster = ((2,000,000) / (3.567)) / ((2,000,000) / (7.726))

    Relate this speedup back to the host environment, and discuss the
    degree of improvement between the sequential and parallel programs.

        adding more cores for the computations to be distributed across created a speed up in execution time.
        The increase in speedup was about half the time of the sequential once made parallel.

    b. what is the efficiency of the parallel program? Discuss.

        2.16 / 12 = 18.04 % - this program is not embarrassingly parallel
        and there are a number of components that needed to remain sequential.

    5. experiment with schedule=XXX and chunk=YYY as command line arguments
    to your program (see edu.rit.pj2.Schedule for XXX values to try, and
    choose various YYY values appropriate to your schedule picks.)
    What did you find in terms of execution times and speedups/slowdowns
    for the various choices you made.
    NOTE: you may have to use a larger input range to evaluate schedules.

        from the data below, I found that the dynamic schedule option seemed to yield the
        best execution times for this particular program not changing chunk size.

        "java pj2 debug=makespan tracker=none schedule=XXX FriendlySmp 1 2000000"

        +--------------+---------------------------------------+
        | XXX          |                  Avg msec over 3 runs |
        +--------------+---------------------------------------+
        | fixed        : 4956 - runs about the same as default |
        | dynamic      : 4471 - slightly faster than default   |
        | leapfrog     : 4906 - runs about the same as default |
        | proportional : 4612 - slightly faster than default   |
        | guided       : 4605 - slightly faster than default   |
        +------------------------------------------------------+

        I didn't notice very much change in the execution times when changing the chunk
        size for dynamic, proportional, and guided schedules.

    ============= Feedback ============================
    What did I learn?

        I learned how to envision programs in chunks that could be parallelized.
        Also how to relate speed up and efficiency to a real program example.

    What was easy about this assignment?

        The easy part of this assignment was creating the first part of the sequential program.
        Overall this was a challenging assignment but not overly challenging.

    What gave you trouble? Describe specific problems, you had, if any.

        I had trouble when I was trying to think of what data structure to use for the map reduce.
        I also had a little trouble with implementing the accumulation variables in the parallelFor loop.
        I understood the concept of what they do but not how to implement them correctly.

    What can the instructor do to improve this assignment?

        Possibly make the sequential dependencies less so that the speed up and efficiency numbers are more pronounced.
        But nothing really comes to mind.

    Would you have like to add anything further?

        This was an interesting project that helps with the grasp of parallel programming and its benefits.
