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
        stark - 

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

    b. java pj2 debug=makespan FriendlySmp 1000 1000000


    4. speedup and efficiency

    a. calculate the speedup of #2 above;
    see lecture notes from week 05 or [BCBD] for how to calculate it.

    Relate this speedup back to the host environment, and discuss the
    degree of improvement between the sequential and parallel programs.

    b. what is the efficiency of the parallel program? Discuss.


    5. experiment with schedule=XXX and chunk=YYY as command line arguments
    to your program (see edu.rit.pj2.Schedule for XXX values to try, and
    choose various YYY values appropriate to your schedule picks.)
    What did you find in terms of execution times and speedups/slowdowns
    for the various choices you made.
    NOTE: you may have to use a larger input range to evaluate schedules.



    ============= Feedback ============================
    What did I learn?




    What was easy about this assignment?




    What gave you trouble? Describe specific problems, you had, if any.




    What can the instructor do to improve this assignment?




    Would you have like to add anything further?

